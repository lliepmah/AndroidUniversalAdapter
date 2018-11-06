package ru.lliepmah.lib.exceptions;

import java.util.Collection;
import ru.lliepmah.lib.Builder;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class ErrorHandler {

  private ErrorHandler() {
    throw new AssertionError("Instantiating utility class.");
  }

  public static void errorBuilderCannotHandleItem(Builder builder, Object item) {
    throw new WrongItemException(
        "Builder: " + builder.getClass().getSimpleName() + " cannot handle item: " + item.getClass()
            .getSimpleName());
  }

  public static void errorMoreThanOneBuildersHandleItem(Collection<Builder> builders, Object item) {
    throw new WrongItemException("More than one builders: " + builders + " can handle item: " + item
        .getClass()
        .getSimpleName());
  }

  public static void erroNoOneBuildersHandleItem(Collection<Builder> builders, Object item) {
    throw new WrongItemException("No one from registered holder builders:"
        + builders
        + " can handle item: "
        + item.getClass().getSimpleName());
  }

  public static void erroNoOneBuildersHaveId(Collection<Builder> values, int builderId) {
    throw new WrongItemException(
        "No one from registered holder builders:" + values + " have id= " + builderId);
  }
}
