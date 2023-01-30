package pl.smarthouse.smartchain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@Builder
@Setter
@Getter
public class Step {
  private final @NotNull String conditionDescription;
  private final @NotNull String stepDescription;
  private LocalDateTime startTime;
  // One minute max step duration by default
  private Duration maxDuration;
  private Duration lastDuration;
  Runnable action;
  Predicate condition;
}
