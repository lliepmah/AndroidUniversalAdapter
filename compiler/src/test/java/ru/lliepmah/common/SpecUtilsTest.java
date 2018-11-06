package ru.lliepmah.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * @author Arthur Korchagin on 14.07.17.
 */

@RunWith(JUnit4.class) public class SpecUtilsTest {

  @Test public void typeElementUtils() throws Exception {
    Throwable expectedException = null;

    try {
      Constructor<SpecUtils> constructor = SpecUtils.class.getDeclaredConstructor();
      Assert.assertFalse(constructor.isAccessible());
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (InvocationTargetException exception) {
      expectedException = exception.getCause();
    }

    Assert.assertTrue(expectedException instanceof AssertionError);
  }

}
