package pl.smarthouse.smartchain.model;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Chain {
	List<Step> stepList = new ArrayList<>();
	private final @NotNull String description;
	private Step activeStep;

	public Chain(final String description) {
		final Step initialStep = Step.builder()
				.stepDescription("Standby")
				.action(null)
				.condition(o -> true)
				.conditionDescription("Standby condition")
				.startTime(LocalDateTime.now())
				.build();
		stepList.add(initialStep);
		this.description = description;
		activeStep = initialStep;
	}

	public void addStep(final Step step) {
		stepList.add(step);
	}

	public void setNextStepActive() {
		activeStep = getNextStep();
		activeStep.setStartTime(LocalDateTime.now());
	}

	public void setStandbyStep() {
		activeStep = stepList.get(0);
		activeStep.setStartTime(LocalDateTime.now());
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
