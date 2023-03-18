package com.swd.backend.api.v1.account.forgot;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConfirmOtpResponse {
    private String token;
}
