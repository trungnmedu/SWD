package com.swd.backend.api.v1.account.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String password;

    //Checking request is valid formant require
    public boolean isValidRequest() {
        if (username == null || password == null) {
            return false;
        }
        return username.trim().length() > 0 && password.trim().length() > 0;
    }
}
