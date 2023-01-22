package pl.smarthouse.smartchain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@Builder
@Setter
@Getter
public class Step {
	private final @NotNull String description;
	private LocalDateTime startTime;
	Runnable action;
	Predicate condition;
}
