package pl.smarthouse.smartchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.smarthouse.smartchain.model.dto.ActiveStepDto;
import pl.smarthouse.smartchain.model.dto.ChainEnabledDto;
import pl.smarthouse.smartchain.processor.ChainProcessor;
import pl.smarthouse.smartchain.service.ChainService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chains")
@RequiredArgsConstructor
public class ChainMaintenanceController {
  private final ChainService chainService;

  @GetMapping
  public Flux<ChainProcessor> getChainProcessors() {
    return chainService.getChainProcessors();
  }

  @GetMapping("/enabled")
  public Flux<ChainEnabledDto> getChainsEnabled() {
    return chainService.getChainsEnabled();
  }

  @GetMapping("/activeStep")
  public Flux<ActiveStepDto> getActiveStep() {
    return chainService.getActiveStep();
  }

  @PostMapping("/enabled")
  public Flux<ChainEnabledDto> setChainsEnabled(
      @RequestParam(required = true) final boolean enabled) {
    return chainService.setChainsEnabled(enabled);
  }
}
