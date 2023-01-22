package pl.smarthouse.smartchain.utils;

import lombok.experimental.UtilityClass;
import pl.smarthouse.smartmodule.model.actors.actor.ActorMap;
import pl.smarthouse.smartmodule.model.actors.command.CommandType;
import pl.smarthouse.smartmodule.model.enums.ActorType;

@UtilityClass
public class ActionUtils {
	
	public void setActionToAllActorType(final ActorMap actorMap, final ActorType actorType, final CommandType commandType) {
		actorMap.stream()
				.forEach(
						actor -> {
							if (actorType.equals(actor.getType())) {
								actor.getCommandSet().setCommandType(commandType);
							}
						});
	}
}
