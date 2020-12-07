package ru.iteco.project.exception;

public class UnavailableRoleOperationException extends RuntimeException {


    public UnavailableRoleOperationException() {
        super();
    }

    public UnavailableRoleOperationException(String message) {
        super(message);
    }

    public UnavailableRoleOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableRoleOperationException(Throwable cause) {
        super(cause);
    }

}
