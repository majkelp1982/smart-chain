package pl.smarthouse.smartchain.model.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.smarthouse.smartchain.exception.ChainBuildException;

@Getter
@Setter
@Slf4j
public class Chain {
  boolean enabled;
  private final List<Step> stepList = new ArrayList<>();
  private final @NotNull String description;
  // Default duration 1 minute. (In constructor)
  private @NotNull Duration maxDuration;
  private Step activeStep;

  public Chain(final String description) {
    final Step initialStep =
        Step.builder()
            .stepDescription("Standby")
            .action(() -> {})
            .condition(o -> enabled)
            .conditionDescription("Wait if chain not enabled")
            .startTime(LocalDateTime.now())
            .build();
    stepList.add(initialStep);
    this.description = description;
    activeStep = initialStep;
    maxDuration = Duration.ofMinutes(1);
    enabled = true;
  }

  public void addStep(final Step step) {
    if (step.getMaxDuration() != null && maxDuration.compareTo(step.getMaxDuration()) <= -1) {
      throw new ChainBuildException(
          String.format(
              "Step: %s, has bigger maxDuration than the chain: %s. MaxDuration of chain: %s, of step: %s",
              step.getStepDescription(), getDescription(), maxDuration, step.getMaxDuration()));
    }
    stepList.add(step);
  }

  public void setNextStepActive() {
    activeStep.setLastDuration(Duration.between(activeStep.getStartTime(), LocalDateTime.now()));
    activeStep = getNextStep();
    activeStep.setStartTime(LocalDateTime.now());
  }

  public void setStandbyStep() {
    activeStep.setLastDuration(Duration.between(activeStep.getStartTime(), LocalDateTime.now()));
    activeStep = stepList.get(0);
    activeStep.setStartTime(LocalDateTime.now());
  }

  public Step getStandbyStep() {
    return stepList.get(0);
  }

  public Step getNextStep() {
    final int activeStepIndex = stepList.indexOf(activeStep);
    if (activeStepIndex == stepList.size() - 1) {
      return stepList.get(0);
    } else {
      return stepList.get(activeStepIndex + 1);
    }
  }
}
