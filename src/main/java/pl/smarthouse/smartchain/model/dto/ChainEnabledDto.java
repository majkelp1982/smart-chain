package pl.smarthouse.smartchain.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ChainEnabledDto {
  String chainName;
  boolean enabled;
}
