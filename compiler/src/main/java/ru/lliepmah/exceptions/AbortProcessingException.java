package ru.lliepmah.exceptions;

public class AbortProcessingException extends RuntimeException {

    private ErrorType mErrorType;

    public AbortProcessingException(String message, ErrorType errorType) {
        super(message);
        mErrorType = errorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbortProcessingException that = (AbortProcessingException) o;

        return mErrorType == that.mErrorType;

    }

    @Override
    public int hashCode() {
        return mErrorType != null ? mErrorType.hashCode() : 0;
    }
}
