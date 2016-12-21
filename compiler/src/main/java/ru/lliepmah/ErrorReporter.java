package ru.lliepmah;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import ru.lliepmah.exceptions.AbortProcessingException;
import ru.lliepmah.exceptions.ErrorType;

class ErrorReporter {
    private final Messager mMessager;

    ErrorReporter(ProcessingEnvironment processingEnv) {
        this.mMessager = processingEnv.getMessager();
    }

    /**
     * Issue a compilation error. This method does not throw an exception, since we want to continue
     * processing and perhaps report other errors. It is a good idea to introduce a test case in
     * CompilationTest for any new call to reportError(...) to ensure that we continue correctly after
     * an error.
     *
     * @param msg the text of the warning
     * @param e   the element to which it pertains
     */
    private void reportError(String msg, Element e) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    AbortProcessingException abortWithError(Element element, ErrorType errorType, Object... args) {
        String message = errorType.getMessage(args);
        reportError(message, element);
        return new AbortProcessingException(message, errorType);
    }

}
