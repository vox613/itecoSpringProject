package ru.iteco.project.exception;

/**
 *
 */
public class MismatchedIdException extends RuntimeException {


    public MismatchedIdException() {
        super();
    }

    public MismatchedIdException(String message) {
        super(message);
    }

    public MismatchedIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public MismatchedIdException(Throwable cause) {
        super(cause);
    }

}
