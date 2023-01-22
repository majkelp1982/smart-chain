package pl.smarthouse.smartchain.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.smarthouse.smartchain.model.Chain;

@RequiredArgsConstructor
@Getter
@Slf4j
public class ChainProcessor {
	private static final String CURRENT_STEP = "Current step: {}";
	private final Chain chain;

	public void process() {
		final Runnable action = chain.getActiveStep().getAction();
		if (action instanceof Runnable) {
			action.run();
		}
		while (!chain.getNextStep().getCondition().test(chain.getActiveStep())) {
		}
		chain.setNextStepActive();
		log.info(CURRENT_STEP, chain.getActiveStep().getDescription());

	}
}
