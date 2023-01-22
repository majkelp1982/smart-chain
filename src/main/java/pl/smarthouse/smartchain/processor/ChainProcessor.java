package pl.smarthouse.smartchain.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.smarthouse.smartchain.model.Chain;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Slf4j
public class ChainProcessor {
	private static final String CURRENT_STEP = "Current step: {}, waiting for next condition: {}";
	private static final String CURRENT_STEP_TIMEOUT = "Current step: {} is timeout. Resetting chain to standby";
	private final Chain chain;

	public void process() {
		final Runnable action = chain.getActiveStep().getAction();
		if (action != null) {
			action.run();
		}
		while (!chain.getNextStep().getCondition().test(chain.getActiveStep())) {
			final Duration duration = Duration.between(chain.getActiveStep().getStartTime(), LocalDateTime.now());
			final Duration stepMaxDuration = chain.getActiveStep().getMaxDuration();
			if (stepMaxDuration != null && duration.compareTo(stepMaxDuration) > 0) {
				log.error(CURRENT_STEP_TIMEOUT, chain.getActiveStep().getStepDescription());
				chain.setStandbyStep();
			}
		}
		chain.setNextStepActive();
		log.info(CURRENT_STEP, chain.getActiveStep().getStepDescription(), chain.getNextStep().getConditionDescription());

	}
}
