package ru.lliepmah.exceptions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Arthur Korchagin on 14.07.17.
 *
 *         Suspicious tests, created only for good code coverage
 */
@RunWith(JUnit4.class) public class ErrorTypeTest {

  private static final int ERRORS_COUNT = 12;

  @Test public void valuesTest() {
    ErrorType[] values = ErrorType.values();
    Assert.assertEquals(values.length, ERRORS_COUNT);
  }

  @Test public void valueOfTest() {
    Assert.assertNotNull(ErrorType.valueOf("UNKNOWN"));
  }
}