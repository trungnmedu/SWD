package com.swd.backend.exception;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotLatestTokenResponse {
    private final String message = "Token does not match the latest token.";
    private String token;
}
