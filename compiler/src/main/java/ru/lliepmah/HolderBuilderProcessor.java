package ru.lliepmah;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

import ru.lliepmah.common.SpecUtils;
import ru.lliepmah.common.TypeElementUtils;
import ru.lliepmah.exceptions.ErrorType;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public final class HolderBuilderProcessor extends AbstractProcessor {

    /* Annotations */
    private static final Class<HolderBuilder> ANNOTATION = HolderBuilder.class;
    private static final Class<HolderConstructor> ANNOTATION_CONSTRUCTOR = HolderConstructor.class;

    /* Types */
    private static final ClassName TYPE_BUILDER = ClassName.get("ru.lliepmah.lib", "Builder");
    private static final ClassName TYPE_CLASS = ClassName.get(Class.class);

    private static final TypeName TYPE_DEFAULT_VIEW_HOLDER =
            ClassName.get("ru.lliepmah.lib", "DefaultViewHolder");
    private static final TypeName TYPE_VIEW = ClassName.get("android.view", "View");
    private static final TypeName TYPE_VIEW_GROUP = ClassName.get("android.view", "ViewGroup");
    private static final TypeName TYPE_LAYOUT_INFLATER =
            ClassName.get("android.view", "LayoutInflater");

    /* Constants */
    private static final String CONSTANT_HOLDER_ID = "HOLDER_ID";
    private static final String CONSTANT_MODEL_CLASS = "MODEL_CLASS";

    /* Methods */
    private static final String METHOD_GET_ID = "getId";
    private static final String METHOD_BUILD = "build";
    private static final String METHOD_GET_HOLDER_CLASS = "getHolderClass";

    /* Variables */
    private static final String VARIABLE_PARENT = "parent";

    /* Postfix */
    private static final String POSTFIX_BUILDER = "Builder";

    private int mVersion = 0;
    private ErrorReporter mErrorReporter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mErrorReporter = new ErrorReporter(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(ANNOTATION);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(ANNOTATION);
        List<TypeElement> types =
                new ImmutableList.Builder<TypeElement>().addAll(ElementFilter.typesIn(annotatedElements))
                        .build();

        for (TypeElement type : types) {
            processType(type);
        }
        return true;
    }

    private void processType(TypeElement type) {
        if (type == null) {
            throw mErrorReporter.abortWithError(null, ErrorType.TYPE_ELEMENT_IS_NULL);
        } else {
            String fqClassName = generatedSubclassName(type, 0);
            String className = TypeElementUtils.simpleNameOf(fqClassName);
            HolderBuilder holderBuilderAnnotation = type.getAnnotation(ANNOTATION);
            checkTypeElement(type, holderBuilderAnnotation);
            String source = generateCode(holderBuilderAnnotation.value(), type, className);
            writeSourceFile(fqClassName, source, type);
        }
    }

    private void checkTypeElement(TypeElement type, HolderBuilder holderBuilderAnnotation) {
        if (holderBuilderAnnotation == null) {
            throw mErrorReporter.abortWithError(type, ErrorType.COMPILER_BUG,
                    HolderBuilder.class.getName());
        }

        if (type.getKind() != ElementKind.CLASS) {
            throw mErrorReporter.abortWithError(type, ErrorType.ANNOTATION_ONLY_FOR_CLASSES,
                    HolderBuilder.class.getName());
        }

        if (TypeElementUtils.isAbstract(type)) {
            throw mErrorReporter.abortWithError(type, ErrorType.CLASS_MUST_NOT_BE_ABSTRACT, type);
        }

        TypeName superClassName = ClassName.get(MoreTypes.asTypeElement(type.getSuperclass()));
        if (!TYPE_DEFAULT_VIEW_HOLDER.equals(superClassName)) {
            throw mErrorReporter.abortWithError(type, ErrorType.UNEXPECTED_SUPERCLASS_OF_TYPE, type,
                    TYPE_DEFAULT_VIEW_HOLDER, superClassName);
        }

        List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
        if (typeParameters != null) {
            if (typeParameters.size() > 0) {
                throw mErrorReporter.abortWithError(typeParameters.get(0), ErrorType.ILLEGAL_TYPE_PARAMETER,
                        type, typeParameters);
            }
        }
    }

    private void checkConstructorParameters(TypeElement type,
                                            List<? extends VariableElement> parameters, ExecutableElement constructor) {
        if (parameters.size() == 0) {
            throw mErrorReporter.abortWithError(constructor,
                    ErrorType.CONSTRUCTOR_MUST_HAVE_LEAST_ONE_PARAMETER, type);
        }
        VariableElement firstParameter = parameters.get(0);

        if (!TYPE_VIEW.equals(ClassName.get(firstParameter.asType()))) {
            throw mErrorReporter.abortWithError(firstParameter,
                    ErrorType.UNEXPECTED_FIRST_PARAMETER_OF_CONSTRUCTOR, type, TYPE_VIEW,
                    firstParameter.asType());
        }
    }

    private String generateCode(@LayoutRes int layout, TypeElement type, String className) {

        List<? extends VariableElement> parameters = findConstructorParameters(type);

        TypeMirror superclassTypeMirror = type.getSuperclass();
        TypeMirror modelTypeMirror =
                MoreTypes.asDeclared(superclassTypeMirror).getTypeArguments().get(0);
        List<? extends VariableElement> elements = parameters.subList(1, parameters.size());
        ImmutableList<FieldSpec> fields = SpecUtils.buildFields(elements);
        ImmutableList<ParameterSpec> constructorParameters = SpecUtils.buildParameters(elements);
        TypeName parameterizedBuilder =
                ParameterizedTypeName.get(TYPE_BUILDER, ClassName.get(modelTypeMirror));
        String pkg = TypeElementUtils.packageNameOf(type);

        TypeSpec.Builder subClass = TypeSpec.classBuilder(className)
                .superclass(parameterizedBuilder)
                .addFields(fields)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addField(generateVersionConstant())
                .addField(generateClassConstant(modelTypeMirror))
                .addMethod(generateConstructor(constructorParameters))
                .addMethod(generateVersionMethod())
                .addMethod(generateClassMethod(modelTypeMirror))
                .addMethod(generateBuildMethod(ClassName.get(type), superclassTypeMirror, layout, fields));
        JavaFile javaFile = JavaFile.builder(pkg, subClass.build()).build();
        return javaFile.toString();
    }

    @NonNull
    private List<? extends VariableElement> findConstructorParameters(TypeElement type) {

        ExecutableElement constructor = findConstructor(type);

        List<? extends VariableElement> parameters = constructor.getParameters();

        checkConstructorParameters(type, parameters, constructor);
        return parameters;
    }

    @NonNull
    private ExecutableElement findConstructor(TypeElement type) {

        List<ExecutableElement> constructors = TypeElementUtils.getConstructors(type);
        ExecutableElement constructor;

        if (constructors.size() == 1) {
            constructor = constructors.get(0);
        } else {
            List<ExecutableElement> annotatedConstructors =
                    TypeElementUtils.getConstructorsWithAnnotation(constructors, ANNOTATION_CONSTRUCTOR);
            if (annotatedConstructors.size() == 0) {
                throw mErrorReporter.abortWithError(constructors.get(0),
                        ErrorType.NO_ANNOTATION_IN_CONSTRUCTORS, ANNOTATION_CONSTRUCTOR.toString());
            } else if (annotatedConstructors.size() > 1) {
                throw mErrorReporter.abortWithError(annotatedConstructors.get(0),
                        ErrorType.MORE_THAN_ONE_CONSTRUCTORS_HAVE_ANNOTATION,
                        ANNOTATION_CONSTRUCTOR.toString());
            } else {
                constructor = annotatedConstructors.get(0);
            }
        }

        if (constructor == null) {
            throw new AssertionError();
        }

        return constructor;
    }

    /* Methods generators */
    private MethodSpec generateVersionMethod() {

        MethodSpec.Builder build =
                MethodSpec.methodBuilder(METHOD_GET_ID).addAnnotation(Override.class);

        return build.addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT)
                .addStatement("return $L", CONSTANT_HOLDER_ID)
                .build();
    }

    @NonNull
    private MethodSpec generateBuildMethod(TypeName className, TypeMirror typeMirror, int layout,
                                           List<FieldSpec> parameters) {
        MethodSpec.Builder build = MethodSpec.methodBuilder(METHOD_BUILD);

        StringBuilder stringBuilder = new StringBuilder("return new " + className + "(view");
        for (FieldSpec fieldSpec : parameters) {
            stringBuilder.append(",");
            stringBuilder.append(fieldSpec.name);
        }

        stringBuilder.append(")");
        return build.addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TYPE_VIEW_GROUP, VARIABLE_PARENT)
                .returns(ClassName.get(typeMirror))
                .addStatement("$T view = $T.from($L.getContext())" + ".inflate($L, $L, false)", TYPE_VIEW,
                        TYPE_LAYOUT_INFLATER, VARIABLE_PARENT, layout, VARIABLE_PARENT)
                .addStatement(stringBuilder.toString())
                .build();
    }

    private MethodSpec generateConstructor(ImmutableList<ParameterSpec> properties) {
        MethodSpec.Builder builder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameters(properties);
        for (ParameterSpec param : properties) {
            builder.addStatement("this._$N = $N", param.name, param.name);
        }
        return builder.build();
    }

    private MethodSpec generateClassMethod(TypeMirror typeMirror) {
        TypeName parameterizedClass = ParameterizedTypeName.get(TYPE_CLASS, ClassName.get(typeMirror));
        MethodSpec.Builder build =
                MethodSpec.methodBuilder(METHOD_GET_HOLDER_CLASS).addAnnotation(Override.class);
        return build.addModifiers(Modifier.PUBLIC)
                .returns(parameterizedClass)
                .addStatement("return $L", CONSTANT_MODEL_CLASS)
                .build();
    }

    private FieldSpec generateVersionConstant() {
        mVersion++;
        return FieldSpec.builder(TypeName.INT, CONSTANT_HOLDER_ID, Modifier.PUBLIC, Modifier.STATIC,
                Modifier.FINAL).initializer("$L", mVersion).build();
    }

    private FieldSpec generateClassConstant(TypeMirror typeMirror) {
        TypeName parameterizedClass = ParameterizedTypeName.get(TYPE_CLASS, ClassName.get(typeMirror));
        return FieldSpec.builder(parameterizedClass, CONSTANT_MODEL_CLASS, Modifier.PUBLIC,
                Modifier.STATIC, Modifier.FINAL).initializer("$T$L", typeMirror, ".class").build();
    }

    /* End of methods generators */

    @NonNull
    private String generatedSubclassName(TypeElement type, int depth) {
        return generatedClassName(type, Strings.repeat("$", depth) + POSTFIX_BUILDER);
    }

    @NonNull
    private String generatedClassName(final TypeElement type, final String postfix) {
        TypeElement elementType = type;

        String name = elementType.getSimpleName().toString();
        while (elementType.getEnclosingElement() instanceof TypeElement) {
            elementType = (TypeElement) elementType.getEnclosingElement();
            name = elementType.getSimpleName() + "." + name;
        }
        String pkg = TypeElementUtils.packageNameOf(elementType);
        return pkg + "." + name + postfix;
    }

    private void writeSourceFile(String className, String text, TypeElement originatingType) {
        try {
            Filer filer = processingEnv.getFiler();
            JavaFileObject sourceFile = filer.createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try {
                writer.write(text);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
