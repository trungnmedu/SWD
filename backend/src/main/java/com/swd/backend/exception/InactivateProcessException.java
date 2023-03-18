package com.swd.backend.exception;

public class InactivateProcessException extends RuntimeException {
    private String filterMessage;

    public InactivateProcessException(String message) {
        super();
        this.filterMessage = message;
    }

    public String getFilterMessage() {
        return filterMessage;
    }
}
