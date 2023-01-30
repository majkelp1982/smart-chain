package pl.smarthouse.smartchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.smarthouse.smartchain.processor.ChainProcessor;
import pl.smarthouse.smartchain.service.ChainService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/chains")
@RequiredArgsConstructor
public class ChainMaintenanceController {
  private final ChainService chainService;

  @GetMapping
  public Mono<List<ChainProcessor>> getChainProcessors() {
    return Mono.just(chainService.getChainProcessorList());
  }
}
