package pl.smarthouse.smartchain.utils;

import lombok.experimental.UtilityClass;
import pl.smarthouse.smartchain.model.Step;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@UtilityClass
public class PredicateUtils {

	public Predicate<Step> delaySeconds(final int delayInSeconds) {
		return step -> LocalDateTime.now().isAfter(step.getStartTime().plusSeconds(delayInSeconds));
	}
}
