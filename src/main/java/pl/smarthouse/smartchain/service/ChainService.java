package pl.smarthouse.smartchain.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import pl.smarthouse.smartchain.model.core.Chain;
import pl.smarthouse.smartchain.model.dto.ActiveStepDto;
import pl.smarthouse.smartchain.model.dto.ChainEnabledDto;
import pl.smarthouse.smartchain.processor.ChainProcessor;
import pl.smarthouse.smartchain.utils.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class ChainService implements ApplicationListener<ApplicationReadyEvent> {
  private static final String PROCESSING_CHAIN = "Processing chain number: {}, description: {}";
  List<ChainProcessor> chainProcessorList = new ArrayList<>();

  public void addChain(final Chain chain) {
    chainProcessorList.add(new ChainProcessor(chain));
  }

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    log.info("Starting chain service.");
    Flux.fromIterable(chainProcessorList)
        .doOnNext(
            chainProcessor ->
                log.info(
                    PROCESSING_CHAIN,
                    chainProcessorList.indexOf(chainProcessor),
                    chainProcessor.getChain().getDescription()))
        .doOnNext(ChainProcessor::process)
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  public Flux<ChainEnabledDto> setChainsEnabled(final boolean enabled) {
    return Flux.fromIterable(chainProcessorList)
        .flatMap(
            chainProcessor -> {
              chainProcessor.getChain().setEnabled(enabled);
              return Mono.just(chainProcessor);
            })
        .flatMap(chainProcessor -> Mono.just(ModelMapper.toChainEnabledDto(chainProcessor)));
  }

  public Flux<ChainEnabledDto> getChainsEnabled() {
    return Flux.fromIterable(chainProcessorList)
        .flatMap(chainProcessor -> Mono.just(ModelMapper.toChainEnabledDto(chainProcessor)));
  }

  public Flux<ActiveStepDto> getActiveStep() {
    return Flux.fromIterable(chainProcessorList)
        .flatMap(
            chainProcessor -> Mono.just(ModelMapper.toActiveStepDto(chainProcessor.getChain())));
  }

  public Flux<ChainProcessor> getChainProcessors() {
    return Flux.fromIterable(chainProcessorList);
  }
}
