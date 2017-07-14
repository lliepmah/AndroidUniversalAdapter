package ru.lliepmah;

import com.github.stefanbirkner.fishbowl.Fishbowl;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.lliepmah.common.TypeElementUtils;
import ru.lliepmah.exceptions.AbortProcessingException;
import ru.lliepmah.exceptions.ErrorType;

/**
 * Created by Arthur Korchagin on 20.12.16
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClassName.class, MoreTypes.class, TypeElementUtils.class})
public class HolderBuilderProcessorTestPowermock {

    private HolderBuilderProcessor mProcessor;

    private ProcessingEnvironment mProcessingEnvironment;

    @Before
    public void setUp() throws Exception {

        mProcessingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        Messager messager = Mockito.mock(Messager.class);

        Mockito.when(mProcessingEnvironment.getMessager()).thenReturn(messager);
        Mockito.doNothing().when(messager).printMessage(Matchers.any(Diagnostic.Kind.class), Matchers.any(CharSequence.class), Matchers.any(Element.class));

        mProcessor = new HolderBuilderProcessor();
        mProcessor.init(mProcessingEnvironment);

    }

    @Test
    public void writeSourceFileCloseWriter() throws Exception {
        Writer writer = Mockito.mock(Writer.class);
        Mockito.doThrow(new IOException()).when(writer).write(Matchers.any(String.class));
        Mockito.doNothing().when(writer).close();

        JavaFileObject fileObject = Mockito.mock(JavaFileObject.class);
        Mockito.when(fileObject.openWriter()).thenReturn(writer);

        Filer filter = Mockito.mock(Filer.class);
        Mockito.when(filter.createSourceFile(Matchers.any(CharSequence.class), Matchers.any(TypeElement.class)))
                .thenReturn(fileObject);
        Mockito.when(mProcessingEnvironment.getFiler()).thenReturn(filter);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod(
                "writeSourceFile", String.class, String.class, TypeElement.class);
        method.setAccessible(true);

        try {
            method.invoke(mProcessor, "cls", "txt", Mockito.mock(TypeElement.class));
        } catch (Throwable e) {
            /* All Ok */
        }


        Mockito.verify(writer).close();
    }

    @Test
    public void writeSourceFileIoExceptionWithCreateSourceFile() throws Exception {
        IOException exception = Mockito.mock(IOException.class);

        Filer filter = Mockito.mock(Filer.class);
        Mockito.when(filter.createSourceFile(Matchers.any(CharSequence.class), Matchers.any(TypeElement.class)))
                .thenThrow(exception);

        Mockito.when(mProcessingEnvironment.getFiler()).thenReturn(filter);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod(
                "writeSourceFile", String.class, String.class, TypeElement.class);
        method.setAccessible(true);

        method.invoke(mProcessor, "cls", "txt", Mockito.mock(TypeElement.class));

        Mockito.verify(exception).printStackTrace();
    }

    @Test
    public void generatedClassNameWithoutPackage() throws Exception {
        final String TEST_CLASS_NAME = "ClassTest";
        final String TEST_PACKAGE_NAME = "package.test";

        Name packageName = Mockito.mock(Name.class);
        Mockito.when(packageName.toString()).thenReturn(TEST_PACKAGE_NAME);
        Name className = Mockito.mock(Name.class);
        Mockito.when(className.toString()).thenReturn(TEST_CLASS_NAME);

        PackageElement packageElement = Mockito.mock(PackageElement.class);
        Mockito.when(packageElement.getQualifiedName()).thenReturn(packageName);

        TypeElement element = Mockito.mock(TypeElement.class);
        Mockito.when(element.getSimpleName()).thenReturn(packageName);
        Mockito.when(element.getEnclosingElement()).thenReturn(packageElement);

        TypeElement type = Mockito.mock(TypeElement.class);
        Mockito.when(type.getSimpleName()).thenReturn(className);
        Mockito.when(type.getEnclosingElement()).thenReturn(element);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("generatedSubclassName", TypeElement.class, Integer.TYPE);
        method.setAccessible(true);

        MatcherAssert.assertThat(method.invoke(mProcessor, type, 0), Is.is(TEST_PACKAGE_NAME + "." + TEST_PACKAGE_NAME + "." + TEST_CLASS_NAME + "Builder"));
        MatcherAssert.assertThat(method.invoke(mProcessor, type, 1), Is.is(TEST_PACKAGE_NAME + "." + TEST_PACKAGE_NAME + "." + TEST_CLASS_NAME + "$Builder"));
        MatcherAssert.assertThat(method.invoke(mProcessor, type, 2), Is.is(TEST_PACKAGE_NAME + "." + TEST_PACKAGE_NAME + "." + TEST_CLASS_NAME + "$$Builder"));
    }


    @Test
    public void generatedClassName() throws Exception {
        final String TEST = "_test_";

        Name name = Mockito.mock(Name.class);
        Mockito.when(name.toString()).thenReturn(TEST);

        PackageElement element = Mockito.mock(PackageElement.class);
        Mockito.when(element.getQualifiedName()).thenReturn(name);

        TypeElement type = Mockito.mock(TypeElement.class);
        Mockito.when(type.getSimpleName()).thenReturn(name);
        Mockito.when(type.getEnclosingElement()).thenReturn(element);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("generatedSubclassName", TypeElement.class, Integer.TYPE);
        method.setAccessible(true);

        MatcherAssert.assertThat(method.invoke(mProcessor, type, 0), Is.is(TEST + "." + TEST + "Builder"));
        MatcherAssert.assertThat(method.invoke(mProcessor, type, 1), Is.is(TEST + "." + TEST + "$Builder"));
        MatcherAssert.assertThat(method.invoke(mProcessor, type, 2), Is.is(TEST + "." + TEST + "$$Builder"));
    }

    @Test
    public void findConstructorNoConstructors() throws Exception {

        final TypeElement type = Mockito.mock(TypeElement.class);
        Mockito.when(type.getKind()).thenReturn(ElementKind.CLASS);

        ImmutableList<ExecutableElement> constructors = Mockito.mock(ImmutableList.class);
        Mockito.when(constructors.size()).thenReturn(1);
        Mockito.when(constructors.get(0)).thenReturn(null);

        PowerMockito.mockStatic(TypeElementUtils.class);
        Mockito.when(TypeElementUtils.getConstructors(type)).thenReturn(constructors);


        Throwable factException = Fishbowl.exceptionThrownBy(
                () -> {
                    Method method = HolderBuilderProcessor.class.getDeclaredMethod("findConstructor", TypeElement.class);
                    method.setAccessible(true);
                    method.invoke(mProcessor, type);
                }
        );

        Assert.assertNotNull(factException.getCause());
        Assert.assertEquals(AssertionError.class, factException.getCause().getClass());
    }

    @Test
    public void checkTypeElementUnexpectedSuperclassOfType() throws Exception {

        PowerMockito.mockStatic(ClassName.class);
        Mockito.when(ClassName.get(Matchers.any(TypeElement.class))).thenReturn(Mockito.mock(ClassName.class));

        PowerMockito.mockStatic(MoreTypes.class);
        Mockito.when(MoreTypes.asTypeElement(Matchers.any(TypeMirror.class))).thenReturn(Mockito.mock(TypeElement.class));

        final HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);
        final TypeElement type = Mockito.mock(TypeElement.class);
        Mockito.when(type.getKind()).thenReturn(ElementKind.CLASS);

        AbortProcessingException abortProcessingException =
                new AbortProcessingException("", ErrorType.UNEXPECTED_SUPERCLASS_OF_TYPE);

        Throwable factException = Fishbowl.exceptionThrownBy(
                () -> {
                    Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
                    method.setAccessible(true);
                    method.invoke(mProcessor, type, holderBuilderAnnotation);

                }
        );
        MatcherAssert.assertThat(factException.getCause(), Is.is(abortProcessingException));
    }

    @Test
    public void checkTypeElementIllegalTypeParameter() throws Exception {
        ClassName defaultViewHolder = ClassName.get("ru.lliepmah.lib", "DefaultViewHolder");

        PowerMockito.mockStatic(ClassName.class);
        Mockito.when(ClassName.get(Matchers.any(TypeElement.class))).thenReturn(defaultViewHolder);

        PowerMockito.mockStatic(MoreTypes.class);
        Mockito.when(MoreTypes.asTypeElement(Matchers.any(TypeMirror.class))).thenReturn(Mockito.mock(TypeElement.class));

        final HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);
        final TypeElement type = Mockito.mock(TypeElement.class);
        Mockito.when(type.getKind()).thenReturn(ElementKind.CLASS);

        Mockito.doReturn(Arrays.asList(Mockito.mock(TypeParameterElement.class))).when(type).getTypeParameters();

        AbortProcessingException abortProcessingException =
                new AbortProcessingException("", ErrorType.ILLEGAL_TYPE_PARAMETER);

        Throwable factException = Fishbowl.exceptionThrownBy(
                () -> {
                    Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
                    method.setAccessible(true);
                    method.invoke(mProcessor, type, holderBuilderAnnotation);

                }
        );
        MatcherAssert.assertThat(factException.getCause(), Is.is(abortProcessingException));
    }

    @Test
    public void checkTypeElementIllegalTypeParameterFails() throws Exception {
        ClassName defaultViewHolder = ClassName.get("ru.lliepmah.lib", "DefaultViewHolder");

        PowerMockito.mockStatic(ClassName.class);
        Mockito.when(ClassName.get(Matchers.any(TypeElement.class))).thenReturn(defaultViewHolder);

        PowerMockito.mockStatic(MoreTypes.class);
        Mockito.when(MoreTypes.asTypeElement(Matchers.any(TypeMirror.class))).thenReturn(Mockito.mock(TypeElement.class));

        final HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);
        final TypeElement type = Mockito.mock(TypeElement.class);
        Mockito.when(type.getKind()).thenReturn(ElementKind.CLASS);
        Mockito.doReturn(null).when(type).getTypeParameters();


        Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
        method.setAccessible(true);

        try {
            method.invoke(mProcessor, type, holderBuilderAnnotation);
        } catch (Throwable factException) {
            Assert.fail();
        }

    }

}
