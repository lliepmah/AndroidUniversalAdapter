package ru.lliepmah.exceptions;

public enum ErrorType {
        NO_ANNOTATION_IN_CONSTRUCTORS("Found more than one constructors, but no one have annotation %1$s"),
        TYPE_ELEMENT_IS_NULL("TypeElement is null"),
        ANNOTATION_ONLY_FOR_CLASSES("@%1$s only applies to classes"),
        CLASS_MUST_NOT_BE_ABSTRACT("Class %1$s must not be abstract"),
        CLASS_NAME_IS_NULL("Class name is null"),
        MORE_THAN_ONE_CONSTRUCTORS_HAVE_ANNOTATION("More than one constructors have annotation %1$s"),

        UNEXPECTED_FIRST_PARAMETER_OF_CONSTRUCTOR("First parameter is constructor of class %1$s must be %2$s but it is %3$s"),
        CONSTRUCTOR_MUST_HAVE_LEAST_ONE_PARAMETER("Constructor of class %1$s must have least one parameter"),
        COMPILER_BUG("annotation processor for @%1$s was invoked with a type annotated differently; compiler bug? O_o"),
        UNEXPECTED_SUPERCLASS_OF_TYPE("Superclass of %1$s must be %2$s, but it is %3$s"),
        UNKNOWN("");

        String mMessage;

        ErrorType(String message) {
            mMessage = message;
        }

        public String getMessage(Object... args) {
            return String.format(mMessage, args);
        }
    }