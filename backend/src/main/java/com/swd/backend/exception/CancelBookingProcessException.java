package com.swd.backend.exception;

public class CancelBookingProcessException extends RuntimeException {
    private String filterMessage;

    public CancelBookingProcessException(String message) {
        super();
        this.filterMessage = message;
    }

    public String getFilterMessage() {
        return filterMessage;
    }
}
