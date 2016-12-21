package ru.lliepmah;

import com.github.stefanbirkner.fishbowl.Fishbowl;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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

import ru.lliepmah.common.TypeElementUtils;
import ru.lliepmah.exceptions.AbortProcessingException;
import ru.lliepmah.exceptions.ErrorType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Arthur Korchagin on 20.12.16
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClassName.class, MoreTypes.class, TypeElementUtils.class})
public class HolderBuilderProcessorTestPowermock {

    private HolderBuilderProcessor mProcessor;

    private ProcessingEnvironment mProcessingEnvironment;
    private Messager mMessager;

    @Before
    public void setUp() throws Exception {

        mProcessingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        mMessager = Mockito.mock(Messager.class);

        Mockito.when(mProcessingEnvironment.getMessager()).thenReturn(mMessager);
        doNothing().when(mMessager).printMessage(any(Diagnostic.Kind.class), any(CharSequence.class), any(Element.class));

        mProcessor = new HolderBuilderProcessor();
        mProcessor.init(mProcessingEnvironment);

    }

    @Test
    public void writeSourceFile_CloseWriter() throws Exception {
        Writer writer = mock(Writer.class);
        doThrow(new IOException()).when(writer).write(any(String.class));
        doNothing().when(writer).close();

        JavaFileObject fileObject = mock(JavaFileObject.class);
        when(fileObject.openWriter()).thenReturn(writer);

        Filer filter = mock(Filer.class);
        when(filter.createSourceFile(any(CharSequence.class), any(TypeElement.class)))
                .thenReturn(fileObject);
        when(mProcessingEnvironment.getFiler()).thenReturn(filter);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod(
                "writeSourceFile", String.class, String.class, TypeElement.class);
        method.setAccessible(true);

        try {
            method.invoke(mProcessor, "cls", "txt", mock(TypeElement.class));
        } catch (Throwable e) {
            /* All Ok */
        }


        verify(writer).close();
    }

    @Test
    public void writeSourceFile_IoExceptionWithCreateSourceFile() throws Exception {
        IOException exception = mock(IOException.class);

        Filer filter = mock(Filer.class);
        when(filter.createSourceFile(any(CharSequence.class), any(TypeElement.class)))
                .thenThrow(exception);

        when(mProcessingEnvironment.getFiler()).thenReturn(filter);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod(
                "writeSourceFile", String.class, String.class, TypeElement.class);
        method.setAccessible(true);

        method.invoke(mProcessor, "cls", "txt", mock(TypeElement.class));

        verify(exception).printStackTrace();
    }

    @Test
    public void generatedClassName_withoutPackage() throws Exception {
        final String TEST_CLASS_NAME = "ClassTest";
        final String TEST_PACKAGE_NAME = "package.test";

        Name packageName = Mockito.mock(Name.class);
        when(packageName.toString()).thenReturn(TEST_PACKAGE_NAME);
        Name className = Mockito.mock(Name.class);
        when(className.toString()).thenReturn(TEST_CLASS_NAME);

        PackageElement packageElement = mock(PackageElement.class);
        when(packageElement.getQualifiedName()).thenReturn(packageName);

        TypeElement element = mock(TypeElement.class);
        when(element.getSimpleName()).thenReturn(packageName);
        when(element.getEnclosingElement()).thenReturn(packageElement);

        TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getSimpleName()).thenReturn(className);
        when(type.getEnclosingElement()).thenReturn(element);

        final Method method = HolderBuilderProcessor.class.getDeclaredMethod("generatedSubclassName", TypeElement.class, Integer.TYPE);
        method.setAccessible(true);

        assertThat(method.invoke(mProcessor, type, 0), is(TEST_PACKAGE_NAME + "." + TEST_PACKAGE_NAME + "." + TEST_CLASS_NAME + "Builder"));
        assertThat(method.invoke(mProcessor, type, 1), is(TEST_PACKAGE_NAME + "." + TEST_PACKAGE_NAME + "." + TEST_CLASS_NAME + "$Builder"));
        assertThat(method.invoke(mProcessor, type, 2), is(TEST_PACKAGE_NAME + "." + TEST_PACKAGE_NAME + "." + TEST_CLASS_NAME + "$$Builder"));
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

        assertThat(method.invoke(mProcessor, type, 0), is(TEST + "." + TEST + "Builder"));
        assertThat(method.invoke(mProcessor, type, 1), is(TEST + "." + TEST + "$Builder"));
        assertThat(method.invoke(mProcessor, type, 2), is(TEST + "." + TEST + "$$Builder"));
    }

    @Test
    public void findConstructor_noConstructors() throws Exception {

        final TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getKind()).thenReturn(ElementKind.CLASS);

        ImmutableList<ExecutableElement> constructors = mock(ImmutableList.class);
        when(constructors.size()).thenReturn(1);
        when(constructors.get(0)).thenReturn(null);

        PowerMockito.mockStatic(TypeElementUtils.class);
        when(TypeElementUtils.getConstructors(type)).thenReturn(constructors);


        Throwable factException = Fishbowl.exceptionThrownBy(
                () -> {
                    Method method = HolderBuilderProcessor.class.getDeclaredMethod("findConstructor", TypeElement.class);
                    method.setAccessible(true);
                    method.invoke(mProcessor, type);
                }
        );

        Assert.assertNotNull(factException.getCause());
        Assert.assertEquals(RuntimeException.class, factException.getCause().getClass());
    }

    @Test
    public void checkTypeElement_unexpectedSuperclassOfType() throws Exception {

        PowerMockito.mockStatic(ClassName.class);
        when(ClassName.get(any(TypeElement.class))).thenReturn(mock(ClassName.class));

        PowerMockito.mockStatic(MoreTypes.class);
        when(MoreTypes.asTypeElement(any(TypeMirror.class))).thenReturn(mock(TypeElement.class));

        final HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);
        final TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getKind()).thenReturn(ElementKind.CLASS);

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
    public void checkTypeElement_illegalTypeParameter() throws Exception {
        ClassName defaultViewHolder = ClassName.get("ru.lliepmah.lib", "DefaultViewHolder");

        PowerMockito.mockStatic(ClassName.class);
        when(ClassName.get(any(TypeElement.class))).thenReturn(defaultViewHolder);

        PowerMockito.mockStatic(MoreTypes.class);
        when(MoreTypes.asTypeElement(any(TypeMirror.class))).thenReturn(mock(TypeElement.class));

        final HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);
        final TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getKind()).thenReturn(ElementKind.CLASS);

        doReturn(Arrays.asList(mock(TypeParameterElement.class))).when(type).getTypeParameters();

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
    public void checkTypeElement_legalTypeParameter() throws Exception {
        ClassName defaultViewHolder = ClassName.get("ru.lliepmah.lib", "DefaultViewHolder");

        PowerMockito.mockStatic(ClassName.class);
        when(ClassName.get(any(TypeElement.class))).thenReturn(defaultViewHolder);

        PowerMockito.mockStatic(MoreTypes.class);
        when(MoreTypes.asTypeElement(any(TypeMirror.class))).thenReturn(mock(TypeElement.class));

        final HolderBuilder holderBuilderAnnotation = Mockito.mock(HolderBuilder.class);
        final TypeElement type = Mockito.mock(TypeElement.class);
        when(type.getKind()).thenReturn(ElementKind.CLASS);
        doReturn(null).when(type).getTypeParameters();


        Method method = HolderBuilderProcessor.class.getDeclaredMethod("checkTypeElement", TypeElement.class, HolderBuilder.class);
        method.setAccessible(true);

        try {
            method.invoke(mProcessor, type, holderBuilderAnnotation);
        } catch (Throwable factException) {
            Assert.fail();
        }

    }

}
