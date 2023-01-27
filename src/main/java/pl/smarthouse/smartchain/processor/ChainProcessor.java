package pl.smarthouse.smartchain.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.smarthouse.smartchain.model.Chain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

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
        .timeout(Duration.ofSeconds(5))
        .delayUntil(
            ignore -> {
              //
              while (!chain.getNextStep().getCondition().test(chain.getActiveStep())) {}

              chain.setNextStepActive();
              return Mono.just(chain.getActiveStep());
            })
        .onErrorResume(
            TimeoutException.class,
            e -> {
              log.error(CURRENT_STEP_TIMEOUT, chain.getActiveStep().getStepDescription());
              chain.setStandbyStep();
              return Mono.empty(); // just(chain.getActiveStep());
            })
        .onErrorResume(
            Exception.class,
            e -> {
              log.error(ERROR_CHAIN, e.getMessage(), e);
              return Mono.empty(); // just(chain.getActiveStep());
            })
        .flatMap(
            ignore -> {
              process();
              return Mono.empty();
            })
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }
}
