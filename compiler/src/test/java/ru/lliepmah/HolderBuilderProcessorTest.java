package ru.lliepmah;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import junit.framework.TestCase;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

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
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import ru.lliepmah.exceptions.AbortProcessingException;
import ru.lliepmah.exceptions.ErrorType;

import static com.google.common.truth.Truth.assertAbout;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Arthur Korchagin on 14.12.16
 */

@RunWith(JUnit4.class)
public class HolderBuilderProcessorTest {


    public static final String PATH_LIBRARY_SOURCES = "../library/src/main/java/ru/lliepmah/lib";

    private HolderBuilderProcessor mProcessor;
    private String mJavaSourceCode;

    private ProcessingEnvironment mProcessingEnvironment;
    private Messager mMessager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mProcessingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        mMessager = Mockito.mock(Messager.class);

        Mockito.when(mProcessingEnvironment.getMessager()).thenReturn(mMessager);
        doNothing().when(mMessager).printMessage(any(Diagnostic.Kind.class), any(CharSequence.class), any(Element.class));

        mProcessor = new HolderBuilderProcessor();
        mProcessor.init(mProcessingEnvironment);

        mJavaSourceCode = null;
    }

    @Test
    public void getSupportedAnnotationTypes() throws Exception {
        Set<String> types = mProcessor.getSupportedAnnotationTypes();

        TestCase.assertNotNull(types);

        assertThat(types.size(), is(1));
        assertThat(types.iterator().next(), is(HolderBuilder.class.getName()));


    }


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Test
    public void init() throws Exception {


        JavaFileObject fileObject = JavaFileObjects.forSourceLines("ObjectHolder",
                "package ru.lliepmah.lib;",
                "",
                "import android.view.View;",
                "import ru.lliepmah.HolderBuilder;",
                "",
                "",
                "@HolderBuilder(1)",
                "public class ObjectHolder extends DefaultViewHolder<Object> {",
                "    ",
                "    public ObjectHolder(View itemView) {",
                "        super(itemView);",
                "    }",
                "",
                "    @Override",
                "    public void bind(Object model) {}",
                "    ",
                "}"
        );

        JavaFileObject defaultViewHolderFile = readFile(PATH_LIBRARY_SOURCES, "DefaultViewHolder");
        JavaFileObject builderFile = readFile(PATH_LIBRARY_SOURCES, "Builder");

        assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(fileObject, defaultViewHolderFile, builderFile))

                .processedWith(new HolderBuilderProcessor()).compilesWithoutError()

                .and().generatesFileNamed(CLASS_OUTPUT, "ru.lliepmah.lib", "ObjectHolderBuilder.class")
                .and().generatesSources(loadBuilderSource("ObjectHolder", "ru.lliepmah.lib", 1, "Object", "java.lang"));


//                .generatesFileNamed(CLASS_OUTPUT, "com.lliepmah", "ObjectHolderBuilder");
//                .withContents(ByteSource.wrap("Bogus".getBytes(UTF_8)));

//        assertAbout(javaSource())
//                .that(JavaFileObjects.forResource(Resources.getResource("ModelHolderWithListener.java")))
//                .compilesWithoutError();

    }


    private JavaFileObject loadBuilderSource(final String className, String packageName, int layoutId, String modelClass, String modelPackage) {

        return JavaFileObjects.forSourceLines(className,

                "package ru.lliepmah.lib;",

                "import android.view.LayoutInflater;",
                "import android.view.View;",
                "import android.view.ViewGroup;",
                "import java.lang.Class;",
                "import " + modelPackage + "." + modelClass + ";",
                "import java.lang.Override;",

                "public final class " + className + "Builder extends Builder<" + modelClass + "> {",
                "    public static final int HOLDER_ID = " + layoutId + ";",
                "    public static final Class<" + modelClass + "> MODEL_CLASS = " + modelClass + ".class;",
                "",
                "    public " + className + "Builder() {",
                "    }",
                "    @Override",
                "    public int getId() {",
                "        return HOLDER_ID;",
                "    }",

                "    @Override",
                "    public Class<" + modelClass + "> getHolderClass() {",
                "        return MODEL_CLASS;",
                "    }",

                "    @Override",
                "    public DefaultViewHolder<" + modelClass + "> build(ViewGroup parent) {",
                "        View view = LayoutInflater.from(parent.getContext()).inflate(" + layoutId + ", parent, false);",
                "        return new " + packageName + "." + className + "(view);",
                "    }",

                "}");

    }


    private JavaFileObject readFile(String path, String className) throws IOException {
        StringBuilder sb = new StringBuilder();

        File file = new File(path + "/" + className + ".java");

        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
        } finally {
            br.close();
        }

        return JavaFileObjects.forSourceString(className, sb.toString());
    }


//    private JavaFileObject javaSourceCode(String fullyQualifiedName, String... source) {
//        return JavaFileObjects.forSourceString(fullyQualifiedName, Joiner.on('\n').join(source));
//    }

    @Test
    public void process() throws Exception {

    }

    @Test
    public void generatedClassName_withoutPackage() throws Exception {
        final String TEST = "_test_";

        Name name = Mockito.mock(Name.class);
        when(name.toString()).thenReturn(TEST);

        PackageElement pkgElement = mock(PackageElement.class);
        when(pkgElement.getQualifiedName()).thenReturn(name);

        TypeElement element = mock(TypeElement.class);
        when(element.getSimpleName()).thenReturn(name);
        when(element.getEnclosingElement()).thenReturn(pkgElement);

        TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getSimpleName()).thenReturn(name);
        when(type.getEnclosingElement()).thenReturn(element);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("generatedSubclassName", TypeElement.class, Integer.TYPE);
        method.setAccessible(true);

        assertThat((String) method.invoke(mProcessor, type, 0), is(TEST + "." + TEST + "." + TEST + "Builder"));
        assertThat((String) method.invoke(mProcessor, type, 1), is(TEST + "." + TEST + "." + TEST + "$Builder"));
        assertThat((String) method.invoke(mProcessor, type, 2), is(TEST + "." + TEST + "." + TEST + "$$Builder"));
    }

    @Test
    public void generatedClassName() throws Exception {
        final String TEST = "_test_";

        Name name = Mockito.mock(Name.class);
        when(name.toString()).thenReturn(TEST);

        PackageElement element = mock(PackageElement.class);
        when(element.getQualifiedName()).thenReturn(name);

        TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getSimpleName()).thenReturn(name);
        when(type.getEnclosingElement()).thenReturn(element);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("generatedSubclassName", TypeElement.class, Integer.TYPE);
        method.setAccessible(true);

        assertThat((String) method.invoke(mProcessor, type, 0), is(TEST + "." + TEST + "Builder"));
        assertThat((String) method.invoke(mProcessor, type, 1), is(TEST + "." + TEST + "$Builder"));
        assertThat((String) method.invoke(mProcessor, type, 2), is(TEST + "." + TEST + "$$Builder"));
    }

    @Test
    public void checkTypeElement_classMustNotBeAbstract() throws Exception {
        expectErrorType(ErrorType.CLASS_MUST_NOT_BE_ABSTRACT);
        TypeElement type = Mockito.mock(TypeElement.class);

        Set<Modifier> modifiers = new HashSet<Modifier>() {{
            add(Modifier.ABSTRACT);
        }};
        when(type.getModifiers()).thenReturn(modifiers);
        when(type.getKind()).thenReturn(ElementKind.CLASS);

        HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
        method.setAccessible(true);
        method.invoke(mProcessor, type, holderBuilderAnnotation);
    }

    @Test
    public void checkTypeElement_annotationOnlyForClasses() throws Exception {
        expectErrorType(ErrorType.ANNOTATION_ONLY_FOR_CLASSES);
        TypeElement type = Mockito.mock(TypeElement.class);
        HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
        method.setAccessible(true);
        method.invoke(mProcessor, type, holderBuilderAnnotation);
    }

    @Test
    public void checkTypeElement_compilerBug() throws Exception {
        expectErrorType(ErrorType.COMPILER_BUG);
        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
        method.setAccessible(true);
        method.invoke(mProcessor, null, null);
    }

    @Test
    public void processType_typeElementIsNull() throws Exception {
        expectErrorType(ErrorType.TYPE_ELEMENT_IS_NULL);
        invokePrivate("processType", mProcessor, TypeElement.class, (TypeElement) null);
    }

    @Test
    public void findConstructorParameters_noAnnotationInConstructors() throws Exception {
        expectErrorType(ErrorType.NO_ANNOTATION_IN_CONSTRUCTORS);

        ExecutableElement constructor1 = mock(ExecutableElement.class);
        when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        ExecutableElement constructor2 = mock(ExecutableElement.class);
        when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);

        TypeElement typeElement = Mockito.mock(TypeElement.class);
        Mockito.doReturn(Arrays.asList(constructor1, constructor2)).when(typeElement).getEnclosedElements();

        invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);
    }

    @Test
    public void findConstructorParameters_unexpectedFirstParameterOfConstructor() throws Exception {
        expectErrorType(ErrorType.UNEXPECTED_FIRST_PARAMETER_OF_CONSTRUCTOR);

        ExecutableElement constructor1 = mock(ExecutableElement.class);
        when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        when(constructor1.getAnnotation(any(Class.class))).thenReturn(mock(Annotation.class));
        VariableElement firstParameter = mock(VariableElement.class);
        when(firstParameter.asType()).thenReturn(mock(TypeMirror.class));
        doReturn(Arrays.asList(firstParameter)).when(constructor1).getParameters();

        ExecutableElement constructor2 = mock(ExecutableElement.class);
        when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);

        TypeElement typeElement = Mockito.mock(TypeElement.class);
        Mockito.doReturn(Arrays.asList(constructor1, constructor2)).when(typeElement).getEnclosedElements();

        Object constructorParameters = invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);

        assertThat(constructorParameters, not(null));
    }

    private void expectErrorType(ErrorType errorType) {
        AbortProcessingException abortProcessingException =
                new AbortProcessingException("", errorType);

        thrown.expect(InvocationTargetException.class);
        thrown.expectCause(IsEqual.equalTo(abortProcessingException));
    }

    @Test
    public void findConstructorParameters_constructorMustHaveLeastOneParameter() throws Exception {
        expectErrorType(ErrorType.CONSTRUCTOR_MUST_HAVE_LEAST_ONE_PARAMETER);

        ExecutableElement constructor1 = mock(ExecutableElement.class);
        when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        when(constructor1.getAnnotation(any(Class.class))).thenReturn(mock(Annotation.class));

        ExecutableElement constructor2 = mock(ExecutableElement.class);
        when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);

        TypeElement typeElement = Mockito.mock(TypeElement.class);
        Mockito.doReturn(Arrays.asList(constructor1, constructor2)).when(typeElement).getEnclosedElements();

        invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);
    }

    @Test
    public void findConstructorParameters_moreThanOneConstructorsHaveAnnotation() throws Exception {
        expectErrorType(ErrorType.MORE_THAN_ONE_CONSTRUCTORS_HAVE_ANNOTATION);

        ExecutableElement constructor1 = mock(ExecutableElement.class);
        when(constructor1.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        when(constructor1.getAnnotation(any(Class.class))).thenReturn(mock(Annotation.class));

        ExecutableElement constructor2 = mock(ExecutableElement.class);
        when(constructor2.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        when(constructor2.getAnnotation(any(Class.class))).thenReturn(mock(Annotation.class));

        TypeElement typeElement = Mockito.mock(TypeElement.class);
        Mockito.doReturn(Arrays.asList(constructor1, constructor2)).when(typeElement).getEnclosedElements();

        invokePrivate("findConstructorParameters", mProcessor, TypeElement.class, typeElement);
    }

    private void invokePrivate(String methodName, HolderBuilderProcessor object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method method = HolderBuilderProcessor.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(object);
    }

    private static <T> Object invokePrivate(String methodName, HolderBuilderProcessor object, Class<T> argumentsType, T... arguments) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method method = HolderBuilderProcessor.class.getDeclaredMethod(methodName, argumentsType);
        method.setAccessible(true);
        return method.invoke(object, arguments);
    }


}
