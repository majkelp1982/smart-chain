package pl.smarthouse.smartchain.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ActiveStepDto {
  private String chainName;
  private String activeStepDescription;
  private String nextStepCondition;
}
