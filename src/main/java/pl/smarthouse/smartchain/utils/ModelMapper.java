package pl.smarthouse.smartchain.utils;

import lombok.experimental.UtilityClass;
import pl.smarthouse.smartchain.model.core.Chain;
import pl.smarthouse.smartchain.model.dto.ActiveStepDto;
import pl.smarthouse.smartchain.model.dto.ChainEnabledDto;
import pl.smarthouse.smartchain.processor.ChainProcessor;

@UtilityClass
public class ModelMapper {
  public ChainEnabledDto toChainEnabledDto(final ChainProcessor chainProcessor) {
    return ChainEnabledDto.builder()
        .chainName(chainProcessor.getChain().getDescription())
        .enabled(chainProcessor.getChain().isEnabled())
        .build();
  }

  public ActiveStepDto toActiveStepDto(final Chain chain) {
    return ActiveStepDto.builder()
        .chainName(chain.getDescription())
        .activeStepDescription(chain.getActiveStep().getStepDescription())
        .nextStepCondition(chain.getNextStep().getConditionDescription())
        .build();
  }
}
