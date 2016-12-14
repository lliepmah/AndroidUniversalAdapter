package ru.lliepmah;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import ru.lliepmah.common.SpecUtils;
import ru.lliepmah.common.TypeElementUtils;
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
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
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

@SuppressLint("NewApi")
@AutoService(Processor.class)
public final class HolderBuilderProcessor extends AbstractProcessor {

    /* Annotations */
    private static final Class<HolderBuilder> ANNOTATION = HolderBuilder.class;
    private static final Class<HolderConstructor> ANNOTATION_CONSTRUCTOR = HolderConstructor.class;

    /* Types */
    private static final ClassName TYPE_BUILDER = ClassName.get("ru.lliepmah.lib", "Builder");
    private static final ClassName TYPE_CLASS = ClassName.get(Class.class);

    private static final TypeName TYPE_DEFAULT_VIEW_HOLDER = ClassName.get("ru.lliepmah.lib", "DefaultViewHolder");
    private static final TypeName TYPE_VIEW = ClassName.get("android.view", "View");
    private static final TypeName TYPE_VIEW_GROUP = ClassName.get("android.view", "ViewGroup");
    private static final TypeName TYPE_CONTEXT = ClassName.get("android.content", "Context");
    private static final TypeName TYPE_LAYOUT_INFLATER = ClassName.get("android.view", "LayoutInflater");

    /* Constants */
    private static final String CONSTANT_HOLDER_ID = "HOLDER_ID";
    private static final String CONSTANT_MODEL_CLASS = "MODEL_CLASS";

    /* Methods */
    private static final String METHOD_GET_ID = "getId";
    private static final String METHOD_BUILD = "build";
    private static final String METHOD_GET_HOLDER_CLASS = "getHolderClass";

    /* Variables */
    private static final String VARIABLE_CONTEXT = "context";
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
        Set<String> types = new LinkedHashSet<String>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<Class<? extends Annotation>>();
        annotations.add(ANNOTATION);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Collection<? extends Element> annotatedElements =
                env.getElementsAnnotatedWith(ANNOTATION);
        List<TypeElement> types = new ImmutableList.Builder<TypeElement>()
                .addAll(ElementFilter.typesIn(annotatedElements))
                .build();

        for (TypeElement type : types) {
            processType(type);
        }
        return true;
    }

    private void processType(TypeElement type) {
        if (type == null) {
            mErrorReporter.abortWithError("TypeElement is null", null);
            return;
        }

        String fqClassName = generatedSubclassName(type, 0);
        String className = TypeElementUtils.simpleNameOf(fqClassName);
        if (className == null) {
            mErrorReporter.abortWithError("Class name is null", type);
        }

        HolderBuilder holderBuilderAnnotation = type.getAnnotation(ANNOTATION);
        if (!checkTypeElement(type, holderBuilderAnnotation)) {
            return;
        }

        String source = generateCode(holderBuilderAnnotation.value(), type, className);
        source = Reformatter.fixup(source);

        writeSourceFile(fqClassName, source, type);
    }

    private boolean checkTypeElement(TypeElement type, HolderBuilder holderBuilderAnnotation) {
        if (holderBuilderAnnotation == null) {
            mErrorReporter.abortWithError("annotation processor for @HolderBuilder was invoked with a"
                    + "type annotated differently; compiler bug? O_o", type);
            return false;
        }
        if (type.getKind() != ElementKind.CLASS) {
            mErrorReporter.abortWithError("@" + HolderBuilder.class.getName() + " only applies to classes", type);
            return false;
        }
        if (TypeElementUtils.isAbstract(type)) {
            mErrorReporter.abortWithError("Class " + type + " must not be abstract", type);
            return false;
        }
        TypeName superClassName = ClassName.get(MoreTypes.asTypeElement(type.getSuperclass()));
        if (!TYPE_DEFAULT_VIEW_HOLDER.equals(superClassName)) {
            mErrorReporter.abortWithError("Superclass of " + type + " must be " + TYPE_DEFAULT_VIEW_HOLDER
                    + ", but it is " + superClassName, type);
            return false;
        }
        List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
        if (typeParameters != null && typeParameters.size() > 0) {
            mErrorReporter.abortWithError("Class " + type + " must not have parameters=<" + typeParameters + ">",
                    typeParameters.get(0));
            return false;
        }
        return true;
    }

    private boolean checkConstructorParameters(TypeElement type,
                                               List<? extends VariableElement> parameters,
                                               ExecutableElement constructor) {
        if (parameters.size() == 0) {
            mErrorReporter.abortWithError("Constructor of class " + type + " must have least one parameter",
                    constructor);
            return false;
        }
        VariableElement firstParameter = parameters.get(0);


        if (!TYPE_VIEW.equals(ClassName.get(firstParameter.asType()))) {
            mErrorReporter.abortWithError("First parameter is constructor of class " + type
                    + " must be " + TYPE_VIEW + " but it is " + firstParameter.asType(), firstParameter);
            return false;
        }
        return true;
    }

    private String generateCode(@LayoutRes int layout, TypeElement type, String className) {

        List<? extends VariableElement> parameters = findConstructorParameters(type);
        if (parameters == null) {
            return null;
        }

        TypeMirror superclassTypeMirror = type.getSuperclass();
        TypeMirror modelTypeMirror = MoreTypes.asDeclared(superclassTypeMirror).getTypeArguments().get(0);
        List<? extends VariableElement> elements = parameters.subList(1, parameters.size());
        ImmutableList<FieldSpec> fields = SpecUtils.buildFields(elements);
        ImmutableList<ParameterSpec> constructorParameters = SpecUtils.buildParameters(elements);
        TypeName parameterizedBuilder = ParameterizedTypeName.get(TYPE_BUILDER, ClassName.get(modelTypeMirror));
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

    private List<? extends VariableElement> findConstructorParameters(TypeElement type) {
        List<ExecutableElement> constructors = TypeElementUtils.getConstructors(type);
        ExecutableElement constructor = null;
        if (constructors.size() == 1) {
            constructor = constructors.get(0);
        } else {
            List<ExecutableElement> annotatedConstructors =
                    TypeElementUtils.getConstructorsWithAnnotation(constructors, ANNOTATION_CONSTRUCTOR);
            if (annotatedConstructors.size() == 0) {
                mErrorReporter.abortWithError("Found more than one constructors, but no one have annotation "
                        + ANNOTATION_CONSTRUCTOR, constructors.get(0));
            } else if (annotatedConstructors.size() > 1) {
                mErrorReporter.abortWithError("More than one constructors have annotation "
                        + ANNOTATION_CONSTRUCTOR, annotatedConstructors.get(0));
            } else {
                constructor = annotatedConstructors.get(0);
            }
        }
        if (constructor == null) {
            return null;
        }
        List<? extends VariableElement> parameters = constructor.getParameters();
        return checkConstructorParameters(type, parameters, constructor) ? parameters : null;
    }

    /* Methods generators */
    private MethodSpec generateVersionMethod() {

        MethodSpec.Builder build = MethodSpec.methodBuilder(METHOD_GET_ID)
                .addAnnotation(Override.class);

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

//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_item_crime, parent, false);
//        View parent
        stringBuilder.append(")");
        return build.addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TYPE_VIEW_GROUP, VARIABLE_PARENT)
                .returns(ClassName.get(typeMirror))
                .addStatement("$T view = $T.from($L.getContext())"
                                + ".inflate($L, $L, false)", TYPE_VIEW, TYPE_LAYOUT_INFLATER,
                        VARIABLE_PARENT, layout, VARIABLE_PARENT)
                .addStatement(stringBuilder.toString())
                .build();
    }

    private MethodSpec generateConstructor(ImmutableList<ParameterSpec> properties) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameters(properties);
        for (ParameterSpec param : properties) {
            builder.addStatement("this._$N = $N", param.name, param.name);
        }
        return builder.build();
    }

    private MethodSpec generateClassMethod(TypeMirror typeMirror) {
        TypeName parameterizedClass = ParameterizedTypeName.get(TYPE_CLASS, ClassName.get(typeMirror));
        MethodSpec.Builder build = MethodSpec.methodBuilder(METHOD_GET_HOLDER_CLASS)
                .addAnnotation(Override.class);
        return build.addModifiers(Modifier.PUBLIC)
                .returns(parameterizedClass)
                .addStatement("return $L", CONSTANT_MODEL_CLASS)
                .build();
    }

    private FieldSpec generateVersionConstant() {
        mVersion++;
        return FieldSpec.builder(TypeName.INT, CONSTANT_HOLDER_ID, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", mVersion)
                .build();
    }

    private FieldSpec generateClassConstant(TypeMirror typeMirror) {
        TypeName parameterizedClass = ParameterizedTypeName.get(TYPE_CLASS, ClassName.get(typeMirror));
        return FieldSpec.builder(parameterizedClass, CONSTANT_MODEL_CLASS,
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$T$L", typeMirror, ".class")
                .build();
    }

    /* End of methods generators */


    private String generatedSubclassName(TypeElement type, int depth) {
        return generatedClassName(type, Strings.repeat("$", depth) + POSTFIX_BUILDER);
    }

    private String generatedClassName(TypeElement type, String postfix) {
        String name = type.getSimpleName().toString();
        while (type.getEnclosingElement() instanceof TypeElement) {
            type = (TypeElement) type.getEnclosingElement();
            name = type.getSimpleName() + "." + name;
        }
        String pkg = TypeElementUtils.packageNameOf(type);
        String dot = pkg.isEmpty() ? "" : ".";
        return pkg + dot + name + postfix;
    }

    private void writeSourceFile(
            String className,
            String text,
            TypeElement originatingType) {
        try {
            JavaFileObject sourceFile =
                    processingEnv.getFiler().
                            createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try {
                writer.write(text);
            } finally {
                //noinspection ThrowFromFinallyBlock
                writer.close();
            }
        } catch (IOException e) { /* silent */ }
    }
}
