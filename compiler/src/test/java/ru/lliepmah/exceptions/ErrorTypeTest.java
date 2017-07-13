package ru.lliepmah.exceptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Arthur Korchagin on 14.07.17.
 *
 * Suspicious tests, created only for good code coverage
 */
@RunWith(JUnit4.class) public class ErrorTypeTest {

  private static final int ERRORS_COUNT = 11;

  @Test public void valuesTest() {
    ErrorType[] values = ErrorType.values();
    assertEquals(values.length, ERRORS_COUNT);
  }

  @Test public void valueOfTest() {
    assertNotNull(ErrorType.valueOf("UNKNOWN"));
  }
}