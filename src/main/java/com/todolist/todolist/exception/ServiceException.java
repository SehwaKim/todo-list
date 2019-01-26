package com.todolist.todolist.exception;

public class ServiceException extends RuntimeException {
    private ExceptionType type;

    public ServiceException(ExceptionType type) {
        this.type = type;
    }

    public String getName() {
        return type.name();
    }

    public String getMessage() {
        return type.getMessage();
    }
}
