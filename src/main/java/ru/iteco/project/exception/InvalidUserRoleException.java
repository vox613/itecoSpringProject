package ru.iteco.project.exception;

public class InvalidUserRoleException extends RuntimeException {


    public InvalidUserRoleException() {
        super();
    }

    public InvalidUserRoleException(String message) {
        super(message);
    }

    public InvalidUserRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserRoleException(Throwable cause) {
        super(cause);
    }

}
