package pl.smarthouse.smartchain.utils;

import lombok.experimental.UtilityClass;
import pl.smarthouse.smartmodule.model.actors.actor.ActorMap;
import pl.smarthouse.smartmodule.model.actors.command.CommandType;
import pl.smarthouse.smartmodule.model.enums.ActorType;

@UtilityClass
public class ActionUtils {

  public void setActionToAllActorType(
      final ActorMap actorMap,
      final ActorType actorType,
      final CommandType commandType,
      final String commandValue) {
    actorMap.stream()
        .forEach(
            actor -> {
              if (actorType.equals(actor.getType())) {
                actor.getCommandSet().setCommandType(commandType);
                actor.getCommandSet().setValue(commandValue);
              }
            });
  }

  public void setActionToAllActorType(
      final ActorMap actorMap, final ActorType actorType, final CommandType commandType) {
    setActionToAllActorType(actorMap, actorType, commandType, null);
  }
}
