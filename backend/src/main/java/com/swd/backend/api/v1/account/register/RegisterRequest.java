package com.swd.backend.api.v1.account.register;

import com.swd.backend.utils.RegexHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String fullName;
    private String password;
    private String phone;
    private String confirmPassword;

    //Check valid request form
    public boolean isValidRequest() {
        return email != null && fullName != null && password != null && confirmPassword != null;
    }

    public String checkBusinessError() {
        email = email.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();
        fullName = fullName.trim();

        if (!email.matches(RegexHelper.EMAIL_REGEX)) {
            return "Email is not valid.";
        }

        if (password.length() < 8) {
            return "Password must at least 8 characters";
        }

        if (!password.matches(confirmPassword)) {
            return "Password and confirm password not matches";
        }
        return null;
    }
}
