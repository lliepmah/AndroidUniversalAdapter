package ru.lliepmah.lib.exceptions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.lliepmah.lib.Builder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Arthur Korchagin on 14.07.17.
 */

@RunWith(JUnit4.class) public class ErrorHandlerTest {

  private Builder mBuilder;

  @Before public void setUp() throws Exception {

    mBuilder = mock(Builder.class);
  }

  @Test public void typeElementUtils() throws Exception {
    Throwable expectedException = null;

    try {
      Constructor<ErrorHandler> constructor = ErrorHandler.class.getDeclaredConstructor();
      assertFalse(constructor.isAccessible());

      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (InvocationTargetException exception) {
      expectedException = exception.getCause();
    }

    assertTrue(expectedException instanceof AssertionError);
  }

  @Test(expected = WrongItemException.class) public void errorBuilderCannotHandleItem()
      throws Exception {
    ErrorHandler.errorBuilderCannotHandleItem(mBuilder, new Object());
  }

  @Test(expected = WrongItemException.class) public void errorMoreThanOneBuildersHandleItem()
      throws Exception {
    ErrorHandler.errorMoreThanOneBuildersHandleItem(Collections.<Builder>emptyList(), new Object());
  }

  @Test(expected = WrongItemException.class) public void erroNoOneBuildersHandleItem()
      throws Exception {
    ErrorHandler.erroNoOneBuildersHandleItem(Arrays.asList(mBuilder), new Object());
  }

  @Test(expected = WrongItemException.class) public void erroNoOneBuildersHaveId() {
    ErrorHandler.erroNoOneBuildersHaveId(Arrays.asList(mBuilder), 0);
  }
}