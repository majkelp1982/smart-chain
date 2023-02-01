package pl.smarthouse.smartchain.utils;

import lombok.experimental.UtilityClass;
import pl.smarthouse.smartchain.model.core.Step;
import pl.smarthouse.smartmodule.model.actors.actor.Actor;
import pl.smarthouse.smartmodule.model.actors.actor.ActorMap;
import pl.smarthouse.smartmodule.model.actors.response.Response;
import pl.smarthouse.smartmodule.model.actors.type.ds18b20.Ds18b20Utils;
import pl.smarthouse.smartmodule.model.enums.ActorType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Predicate;

@UtilityClass
public class PredicateUtils {

  public Predicate<Step> delaySeconds(final int delayInSeconds) {
    return step -> LocalDateTime.now().isAfter(step.getStartTime().plusSeconds(delayInSeconds));
  }

  public Predicate<Step> isResponseUpdated(final Actor actor) {
    return step -> actor.getResponse().getResponseUpdate().isAfter(step.getStartTime());
  }

  public Predicate<Step> isActorReadCommandSuccessful(final Actor actor) {
    return step -> {
      final Response response = actor.getResponse();
      if (Objects.isNull(response) || Objects.isNull(response.getResponseUpdate())) {
        return false;
      } else {
        return step.getStartTime().isBefore(response.getResponseUpdate());
      }
    };
  }

  public Predicate<Step> isErrorOnDs18b20Group(final Response response) {
    return step -> Ds18b20Utils.isErrorOnDs18b20Group(response);
  }

  public Predicate<Step> isAllActorTypeReadCommandSuccessful(
      final ActorMap actorMap, final ActorType actorType) {
    return step ->
        actorMap.stream()
            .filter(actor -> actorType.equals(actor.getType()))
            .allMatch(actor -> isActorReadCommandSuccessful(actor).test(step));
  }
}
