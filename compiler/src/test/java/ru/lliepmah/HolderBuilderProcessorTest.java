package ru.lliepmah;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Arthur Korchagin on 14.12.16
 */

@RunWith(JUnit4.class)
public class HolderBuilderProcessorTest {


    public static final String PATH_LIBRARY_SOURCES = "../library/src/main/java/ru/lliepmah/lib";
    private HolderBuilderProcessor mProcessor;
    private String mJavaSourceCode;

    @Before
    public void setUp() throws Exception {
        mProcessor = new HolderBuilderProcessor();
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

}
