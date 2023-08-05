package pl.smarthouse.smartchain.processor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.smarthouse.smartchain.model.core.Chain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Getter
@Slf4j
public class ChainProcessor {
  private static final String CURRENT_STEP =
      "Chain: {}, Current step: {}, waiting for next condition: {}";
  private static final String CURRENT_STEP_TIMEOUT =
      "Current step: {} is timeout. Resetting chain to standby";
  private static final String ERROR_CHAIN = "During chain processing error occur. Message: {}";
  private final Chain chain;

  public void process() {
    Mono.just(chain.getActiveStep().getAction())
        .doOnNext(
            ignore ->
                log.info(
                    CURRENT_STEP,
                    chain.getDescription(),
                    chain.getActiveStep().getStepDescription(),
                    chain.getNextStep().getConditionDescription()))
        .flatMap(
            runnable -> {
              runnable.run();
              return Mono.just(runnable);
            })
        .delayUntil(
            ignore -> {
              final Duration maxDuration = findMaxDuration();
              while (!chain.getNextStep().getCondition().test(chain.getActiveStep())) {
                if (chain.isEnabled()) {
                  if (!chain.getActiveStep().equals(chain.getStandbyStep())
                      && LocalDateTime.now()
                          .isAfter(chain.getActiveStep().getStartTime().plus(maxDuration))) {
                    log.warn(
                        "Chain: {}, Step: {} timeout. Chain reset to standby. StartTime: {}, CurrentTime: {}",
                        chain.getDescription(),
                        chain.getActiveStep().getStepDescription(),
                        chain.getActiveStep().getStartTime(),
                        LocalDateTime.now());
                    return Mono.error(new TimeoutException());
                  }
                  try {
                    Thread.sleep(1000);
                  } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                  }
                }
              }
              chain.setNextStepActive();
              return Mono.just(chain.getActiveStep());
            })
        .onErrorResume(
            TimeoutException.class,
            e -> {
              log.error(CURRENT_STEP_TIMEOUT, chain.getActiveStep().getStepDescription());
              chain.setStandbyStep();
              return Mono.just(chain.getActiveStep().getAction());
            })
        .onErrorResume(
            Exception.class,
            e -> {
              log.error(ERROR_CHAIN, e.getMessage(), e);
              return Mono.just(chain.getActiveStep().getAction());
            })
        .flatMap(
            signal -> {
              process();
              return Mono.empty();
            })
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  private Duration findMaxDuration() {
    return (chain.getActiveStep().getMaxDuration() != null)
        ? chain.getActiveStep().getMaxDuration()
        : chain.getMaxDuration();
  }
}
