package ru.iteco.project.controller.dto;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

public class ResponseError {

    private UUID id;
    private String message;
    private String codeError;
    private List<ObjectError> objectErrorList;

    public ResponseError() {
    }

    public ResponseError(UUID id, String message, String codeError) {
        this.id = id;
        this.message = message;
        this.codeError = codeError;
    }

    public ResponseError(UUID id, String message, String codeError, List<ObjectError> objectErrorList) {
        this.id = id;
        this.message = message;
        this.codeError = codeError;
        this.objectErrorList = objectErrorList;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodeError() {
        return codeError;
    }

    public void setCodeError(String codeError) {
        this.codeError = codeError;
    }

    public List<ObjectError> getObjectErrorList() {
        return objectErrorList;
    }

    public void setObjectErrorList(List<ObjectError> objectErrorList) {
        this.objectErrorList = objectErrorList;
    }
}
