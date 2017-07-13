package ru.lliepmah.common;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by Arthur Korchagin on 26.10.16
 */

public class TypeElementUtils {

  private TypeElementUtils() {
    throw new AssertionError("Instantiating utility class.");
  }

  public static boolean isAbstract(@NonNull TypeElement element) {
    return element.getModifiers().contains(Modifier.ABSTRACT);
  }

  public static ImmutableList<ExecutableElement> getConstructors(@NonNull TypeElement element) {
    ImmutableList.Builder<ExecutableElement> elementsBuilder = ImmutableList.builder();
    for (Element enclosedElement : element.getEnclosedElements()) {
      if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR
          && enclosedElement instanceof ExecutableElement) {
        elementsBuilder.add((ExecutableElement) enclosedElement);
      }
    }
    return elementsBuilder.build();
  }

  public static ImmutableList<ExecutableElement> getConstructorsWithAnnotation(
      List<ExecutableElement> constructors, Class<? extends Annotation> annotation) {
    ImmutableList.Builder<ExecutableElement> elementsBuilder = ImmutableList.builder();
    for (ExecutableElement element : constructors) {
      if (element.getAnnotation(annotation) != null) {
        elementsBuilder.add(element);
      }
    }
    return elementsBuilder.build();
  }

  public static String packageNameOf(TypeElement type) {
    while (true) {
      Element enclosing = type.getEnclosingElement();
      if (enclosing instanceof PackageElement) {
        return ((PackageElement) enclosing).getQualifiedName().toString();
      }
      type = (TypeElement) enclosing;
    }
  }

  public static String simpleNameOf(String s) {
    if (s.contains(".")) {
      return s.substring(s.lastIndexOf('.') + 1);
    } else {
      return s;
    }
  }
}
