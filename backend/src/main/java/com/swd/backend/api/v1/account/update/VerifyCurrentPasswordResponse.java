package com.swd.backend.api.v1.account.update;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyCurrentPasswordResponse {
    private String token;
    private String message;
}
