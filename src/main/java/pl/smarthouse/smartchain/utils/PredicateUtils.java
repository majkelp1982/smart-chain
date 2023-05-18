package pl.smarthouse.smartchain.utils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pl.smarthouse.smartchain.model.core.Step;
import pl.smarthouse.smartmodule.model.actors.actor.Actor;
import pl.smarthouse.smartmodule.model.actors.actor.ActorMap;
import pl.smarthouse.smartmodule.model.actors.response.Response;
import pl.smarthouse.smartmodule.model.actors.type.ds18b20.Ds18b20Utils;
import pl.smarthouse.smartmodule.model.enums.ActorType;

@UtilityClass
@Slf4j
public class PredicateUtils {
  private static final String WARNING_RESPONSE_UPDATE =
      "Step: {}, response: {}, respponse update is null";
  private static final String WARNING_STEP = "Step: {}, no getStartTime";

  public Predicate<Step> delaySeconds(final int delayInSeconds) {
    return step -> LocalDateTime.now().isAfter(step.getStartTime().plusSeconds(delayInSeconds));
  }

  public Predicate<Step> isResponseUpdated(final Actor actor) {
    return step -> {
      final Response response = actor.getResponse();
      if (!Objects.isNull(response)) {
        if (Objects.isNull(response.getResponseUpdate())) {
          // TODO
          //         log.warn(WARNING_RESPONSE_UPDATE, step, response);
          return false;
        } else {

          if (Objects.isNull(step.getStartTime())) {
            log.warn(WARNING_STEP, step);
            return false;
          }
          return response.getResponseUpdate().isAfter(step.getStartTime());
        }
      }
      return false;
    };
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
