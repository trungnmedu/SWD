package com.swd.backend.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplyVoucherException extends Exception {
    private String errorMessage;
    private String stack;
}
