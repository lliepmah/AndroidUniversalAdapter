package ru.lliepmah.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/**
 * Created by Arthur Korchagin on 21.12.16
 */

@RunWith(JUnit4.class) public class TypeElementUtilsTest {

  @Test public void typeElementUtils() throws Exception {
    Throwable expectedException = null;

    try {
      Constructor<TypeElementUtils> constructor = TypeElementUtils.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
    } catch (InvocationTargetException exception) {
      expectedException = exception.getCause();
    }

    Assert.assertTrue(expectedException instanceof AssertionError);
  }

  @Test public void packageNameOf() throws Exception {
    String NAME = "test.package";

    Name packageName = Mockito.mock(Name.class);
    Mockito.when(packageName.toString()).thenReturn(NAME);

    PackageElement packageElement = Mockito.mock(PackageElement.class);
    Mockito.when(packageElement.getQualifiedName()).thenReturn(packageName);

    Element element = Mockito.mock(TypeElement.class);
    Mockito.when(element.getEnclosingElement()).thenReturn(packageElement);

    TypeElement type = Mockito.mock(TypeElement.class);
    Mockito.when(type.getEnclosingElement()).thenReturn(element);

    MatcherAssert.assertThat(TypeElementUtils.packageNameOf(type), Is.is(NAME));
  }

  @Test public void simpleNameOf() throws Exception {
    String NAME = "Test";
    MatcherAssert.assertThat(TypeElementUtils.simpleNameOf("ru.test.test." + NAME), Is.is(NAME));
    MatcherAssert.assertThat(TypeElementUtils.simpleNameOf("ru.test." + NAME), Is.is(NAME));
    MatcherAssert.assertThat(TypeElementUtils.simpleNameOf(NAME), Is.is(NAME));
    MatcherAssert.assertThat(TypeElementUtils.simpleNameOf(getClass().getName()),
        Is.is(getClass().getSimpleName()));
  }
}