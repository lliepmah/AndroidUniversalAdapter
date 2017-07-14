package ru.lliepmah;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.google.common.base.Preconditions;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mockito;
import ru.lliepmah.exceptions.AbortProcessingException;
import ru.lliepmah.exceptions.ErrorType;

/**
 * Created by Arthur Korchagin on 14.12.16
 */

@RunWith(JUnit4.class) public class HolderBuilderProcessorTest {

  private static final String PATH_LIBRARY_SOURCES = "../library/src/main/java/ru/lliepmah/lib";
  private static final int ONE = 1;
  private static final int TEST_PARAMS_COUNT = 4;

  private HolderBuilderProcessor mProcessor;

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Before public void setUp() throws Exception {
    ProcessingEnvironment processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
    Messager messager = Mockito.mock(Messager.class);

    Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);
    Mockito.doNothing()
        .when(messager)
        .printMessage(Matchers.any(Diagnostic.Kind.class), Matchers.any(CharSequence.class),
            Matchers.any(Element.class));

    mProcessor = new HolderBuilderProcessor();
    mProcessor.init(processingEnvironment);
  }

  @Test public void getSupportedAnnotationTypes() throws Exception {
    Set<String> types = mProcessor.getSupportedAnnotationTypes();

    Assert.assertNotNull(types);

    MatcherAssert.assertThat(types.size(), Is.is(ONE));
    MatcherAssert.assertThat(types.iterator().next(), Is.is(HolderBuilder.class.getName()));
  }

  @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD) @Test public void initWithArguments()
      throws Exception {
    for (int parameterCount = 0; parameterCount < TEST_PARAMS_COUNT; parameterCount++) {
      testGeneratingParameters(parameterCount);
    }

    Assert.assertTrue(true);
  }

  private void testGeneratingParameters(int parameterCount) throws IOException {
    final String testClassName = "TestsParameterClass";
    int layoutId = 3;

    String[] parametersClasses = new String[parameterCount];

    StringBuilder testClassParameters = new StringBuilder();
    StringBuilder testParameters = new StringBuilder();
    StringBuilder testParametersAssign = new StringBuilder();
    StringBuilder testMembersDeclaration = new StringBuilder();

    for (int i = 0; i < parameterCount; i++) {
      String parameterClass = testClassName + i;
      String parameterName = "p" + parameterClass;
      String parameterMemberName = "m" + parameterClass;

      parametersClasses[i] = parameterClass;

      testClassParameters.append("public static class ");
      testClassParameters.append(parameterClass);
      testClassParameters.append("{}\n");

      testParameters.append(", ");
      testParameters.append(parameterClass);
      testParameters.append(" ");
      testParameters.append(parameterName);

      testParametersAssign.append(parameterMemberName);
      testParametersAssign.append("=");
      testParametersAssign.append(parameterName);
      testParametersAssign.append(";\n");

      testMembersDeclaration.append("private final ");
      testMembersDeclaration.append(parameterClass);
      testMembersDeclaration.append(" ");
      testMembersDeclaration.append(parameterMemberName);
      testMembersDeclaration.append(";\n");
    }

    JavaFileObject fileObject =
        generateInputFile(layoutId, testClassParameters.toString(), testParameters.toString(),
            testParametersAssign.toString(), testMembersDeclaration.toString());

    JavaFileObject defaultViewHolderFile = readFile(PATH_LIBRARY_SOURCES, "DefaultViewHolder");
    JavaFileObject builderFile = readFile(PATH_LIBRARY_SOURCES, "Builder");

    Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
        .that(Arrays.asList(fileObject, defaultViewHolderFile, builderFile))
        .processedWith(new HolderBuilderProcessor())
        .compilesWithoutError()
        .and()
        .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "ru.lliepmah.lib",
            "ObjectHolderBuilder.class")
        .and()
        .generatesSources(
            loadBuilderWithParametersSource("ObjectHolder", "ru.lliepmah.lib", layoutId, "Object",
                "java.lang", parametersClasses));
  }

  @NonNull private JavaFileObject generateInputFile(int layoutId, String testClassParameters,
      String testParameters, String testParametersAssign, String testMembersDeclaration) {

    return JavaFileObjects.forSourceLines("ObjectHolder", "package ru.lliepmah.lib;", "",
        "import java.lang.Object;", "import android.view.View;",
        "import ru.lliepmah.HolderBuilder;", "", "", "@HolderBuilder(" + layoutId + ")",
        "public class ObjectHolder extends DefaultViewHolder<Object> {", "    ",
        testMembersDeclaration, "    ",
        "    public ObjectHolder(View itemView" + testParameters + ") {",
        "        super(itemView);", testParametersAssign, "    }", "", "    @Override",
        "    public void bind(Object model) {}", "    ", testClassParameters, "}");
  }

  @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD) @Test public void init() throws Exception {

    JavaFileObject fileObject =
        JavaFileObjects.forSourceLines("ObjectHolder", "package ru.lliepmah.lib;", "",
            "import android.view.View;", "import ru.lliepmah.HolderBuilder;", "", "",
            "@HolderBuilder(1)", "public class ObjectHolder extends DefaultViewHolder<Object> {",
            "    ", "    public ObjectHolder(View itemView) {", "        super(itemView);", "    }",
            "", "    @Override", "    public void bind(Object model) {}", "    ", "}");

    JavaFileObject defaultViewHolderFile = readFile(PATH_LIBRARY_SOURCES, "DefaultViewHolder");
    JavaFileObject builderFile = readFile(PATH_LIBRARY_SOURCES, "Builder");

    Preconditions.checkArgument(true);

    Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
        .that(Arrays.asList(fileObject, defaultViewHolderFile, builderFile))

        .processedWith(new HolderBuilderProcessor())
        .compilesWithoutError()

        .and()
        .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "ru.lliepmah.lib",
            "ObjectHolderBuilder.class")
        .and()
        .generatesSources(
            loadBuilderSource("ObjectHolder", "ru.lliepmah.lib", 1, "Object", "java.lang"));
  }

  private JavaFileObject loadBuilderWithParametersSource(final String className, String packageName,
      int layoutId, String modelClass, String modelPackage, String[] parametersClasses) {

    StringBuilder patametersDeclaration = new StringBuilder();
    StringBuilder patametersSending = new StringBuilder();
    StringBuilder testParametersAssign = new StringBuilder();
    StringBuilder argumens = new StringBuilder();
    String argsList = "";

    for (String parameterClass : parametersClasses) {
      String parameterName = "p" + parameterClass;
      String parameterMemberName = "_" + parameterName;

      patametersDeclaration.append("ObjectHolder.");
      patametersDeclaration.append(parameterClass);
      patametersDeclaration.append(" ");
      patametersDeclaration.append(parameterMemberName);
      patametersDeclaration.append(";\n");

      patametersSending.append(", ");
      patametersSending.append(parameterMemberName);

      testParametersAssign.append("this.");
      testParametersAssign.append(parameterMemberName);
      testParametersAssign.append("=");
      testParametersAssign.append(parameterName);
      testParametersAssign.append(";\n");

      argumens.append("ObjectHolder.");
      argumens.append(parameterClass);
      argumens.append(" ");
      argumens.append(parameterName);
      argumens.append(",");
    }

    if (argumens.length() > 1) {
      argsList = argumens.substring(0, argumens.length() - 1);
    }
    return JavaFileObjects.forSourceLines(className,

        "package ru.lliepmah.lib;",

        "import android.view.LayoutInflater;", "import android.view.View;",
        "import android.view.ViewGroup;", "import java.lang.Class;",
        "import " + modelPackage + "." + modelClass + ";", "import java.lang.Override;",

        "public final class " + className + "Builder extends Builder<" + modelClass + "> {",
        "    public static final int HOLDER_ID = " + 1 + ";",
        "    public static final Class<" + modelClass + "> MODEL_CLASS = " + modelClass + ".class;",

        patametersDeclaration.toString(),

        "", "    public " + className + "Builder(" + argsList + ") {",
        testParametersAssign.toString(),

        "    }", "    @Override", "    public int getId() {", "        return HOLDER_ID;", "    }",

        "    @Override", "    public Class<" + modelClass + "> getHolderClass() {",
        "        return MODEL_CLASS;", "    }",

        "    @Override",
        "    public DefaultViewHolder<" + modelClass + "> build(ViewGroup parent) {",
        "        View view = LayoutInflater.from(parent.getContext()).inflate("
            + layoutId
            + ", parent, false);",
        "        return new " + packageName + "." + className + "(view" + patametersSending + ");",
        "    }",

        "}");
  }

  private JavaFileObject loadBuilderSource(final String className, String packageName, int layoutId,
      String modelClass, String modelPackage) {

    return JavaFileObjects.forSourceLines(className,

        "package ru.lliepmah.lib;",

        "import android.view.LayoutInflater;", "import android.view.View;",
        "import android.view.ViewGroup;", "import java.lang.Class;",
        "import " + modelPackage + "." + modelClass + ";", "import java.lang.Override;",

        "public final class " + className + "Builder extends Builder<" + modelClass + "> {",
        "    public static final int HOLDER_ID = " + layoutId + ";",
        "    public static final Class<" + modelClass + "> MODEL_CLASS = " + modelClass + ".class;",
        "", "    public " + className + "Builder() {", "    }", "    @Override",
        "    public int getId() {", "        return HOLDER_ID;", "    }",

        "    @Override", "    public Class<" + modelClass + "> getHolderClass() {",
        "        return MODEL_CLASS;", "    }",

        "    @Override",
        "    public DefaultViewHolder<" + modelClass + "> build(ViewGroup parent) {",
        "        View view = LayoutInflater.from(parent.getContext()).inflate("
            + layoutId
            + ", parent, false);",
        "        return new " + packageName + "." + className + "(view);", "    }",

        "}");
  }

  private JavaFileObject readFile(String path, String className) throws IOException {
    StringBuilder sb = new StringBuilder();

    File file = new File(path + "/" + className + ".java");

    System.out.println("filepath->" + file.getAbsolutePath());

    BufferedReader br = new BufferedReader(new FileReader(file));
    try {
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }
    } finally {
      br.close();
    }

    return JavaFileObjects.forSourceString(className, sb.toString());
  }

  //    private JavaFileObject javaSourceCode(String fullyQualifiedName, String... source) {
  //        return JavaFileObjects.forSourceString(fullyQualifiedName, Joiner.on('\n').join(source));
  //    }

  @Test public void checkTypeElementClassMustNotBeAbstract() throws Exception {
    expectErrorType(ErrorType.CLASS_MUST_NOT_BE_ABSTRACT);
    TypeElement type = Mockito.mock(TypeElement.class);

    Set<Modifier> modifiers = new HashSet<Modifier>() {{
      add(Modifier.ABSTRACT);
    }};
    Mockito.when(type.getModifiers()).thenReturn(modifiers);
    Mockito.when(type.getKind()).thenReturn(ElementKind.CLASS);

    HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);

    final Method method =
        HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class,
            HolderBuilder.class);
    method.setAccessible(true);
    method.invoke(mProcessor, type, holderBuilderAnnotation);

    Assert.fail();
  }

  @Test public void checkTypeElementAnnotationOnlyForClasses() throws Exception {
    expectErrorType(ErrorType.ANNOTATION_ONLY_FOR_CLASSES);
    TypeElement type = Mockito.mock(TypeElement.class);
    HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);

    final Method method =
        HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class,
            HolderBuilder.class);
    method.setAccessible(true);
    method.invoke(mProcessor, type, holderBuilderAnnotation);
  }

  @Test public void checkTypeElementCompilerBug() throws Exception {
    expectErrorType(ErrorType.COMPILER_BUG);
    final Method method =
        HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class,
            HolderBuilder.class);
    method.setAccessible(true);
    method.invoke(mProcessor, null, null);
  }

  @Test public void processTypeTypeElementIsNull() throws Exception {
    expectErrorType(ErrorType.TYPE_ELEMENT_IS_NULL);
    invokePrivate("processType", mProcessor, TypeElement.class, (TypeElement) null);
  }

  @Test public void findConstructorParametersNoAnnotationInConstructors() throws Exception {
    expectErrorType(ErrorType.NO_ANNOTATION_IN_CONSTRUCTORS);

    ExecutableElement constructor1 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
    ExecutableElement constructor2 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);

    TypeElement typeElement = Mockito.mock(TypeElement.class);
    Mockito.doReturn(Arrays.asList(constructor1, constructor2))
        .when(typeElement)
        .getEnclosedElements();

    invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);
  }

  @Test public void findConstructorParametersUnexpectedFirstParameterOfConstructor()
      throws Exception {
    expectErrorType(ErrorType.UNEXPECTED_FIRST_PARAMETER_OF_CONSTRUCTOR);

    ExecutableElement constructor1 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
    Mockito.when(constructor1.getAnnotation(Matchers.any(Class.class)))
        .thenReturn(Mockito.mock(Annotation.class));
    VariableElement firstParameter = Mockito.mock(VariableElement.class);
    Mockito.when(firstParameter.asType()).thenReturn(Mockito.mock(TypeMirror.class));
    Mockito.doReturn(Arrays.asList(firstParameter)).when(constructor1).getParameters();

    ExecutableElement constructor2 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);

    TypeElement typeElement = Mockito.mock(TypeElement.class);
    Mockito.doReturn(Arrays.asList(constructor1, constructor2))
        .when(typeElement)
        .getEnclosedElements();

    Object constructorParameters =
        invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);

    MatcherAssert.assertThat(constructorParameters, IsNot.not(null));
  }

  private void expectErrorType(ErrorType errorType) {
    AbortProcessingException abortProcessingException = new AbortProcessingException("", errorType);

    thrown.expect(InvocationTargetException.class);
    thrown.expectCause(IsEqual.equalTo(abortProcessingException));
  }

  @Test public void findConstructorParametersConstructorMustHaveLeastOneParameter()
      throws Exception {
    expectErrorType(ErrorType.CONSTRUCTOR_MUST_HAVE_LEAST_ONE_PARAMETER);

    ExecutableElement constructor1 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
    Mockito.when(constructor1.getAnnotation(Matchers.any(Class.class)))
        .thenReturn(Mockito.mock(Annotation.class));

    ExecutableElement constructor2 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);

    TypeElement typeElement = Mockito.mock(TypeElement.class);
    Mockito.doReturn(Arrays.asList(constructor1, constructor2))
        .when(typeElement)
        .getEnclosedElements();

    invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);
  }

  @Test public void findConstructorParametersMoreThanOneConstructorsHaveAnnotation()
      throws Exception {
    expectErrorType(ErrorType.MORE_THAN_ONE_CONSTRUCTORS_HAVE_ANNOTATION);

    ExecutableElement constructor1 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
    Mockito.when(constructor1.getAnnotation(Matchers.any(Class.class)))
        .thenReturn(Mockito.mock(Annotation.class));

    ExecutableElement constructor2 = Mockito.mock(ExecutableElement.class);
    Mockito.when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
    Mockito.when(constructor2.getAnnotation(Matchers.any(Class.class)))
        .thenReturn(Mockito.mock(Annotation.class));

    TypeElement typeElement = Mockito.mock(TypeElement.class);
    Mockito.doReturn(Arrays.asList(constructor1, constructor2))
        .when(typeElement)
        .getEnclosedElements();

    invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);
  }

  private void invokePrivate(String methodName, HolderBuilderProcessor object)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Method method = HolderBuilderProcessor.class.getDeclaredMethod(methodName);
    method.setAccessible(true);
    method.invoke(object);
  }

  private static <T> Object invokePrivate(String methodName, HolderBuilderProcessor object,
      Class<T> argumentsType, T... arguments)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Method method = HolderBuilderProcessor.class.getDeclaredMethod(methodName, argumentsType);
    method.setAccessible(true);
    return method.invoke(object, arguments);
  }
}
