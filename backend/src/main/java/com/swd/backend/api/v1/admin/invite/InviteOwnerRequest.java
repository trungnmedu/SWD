package com.swd.backend.api.v1.admin.invite;

import com.swd.backend.utils.RegexHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InviteOwnerRequest {
    private String email;
    private String fullName;
    private String phone;

    public boolean isValid() {
        if (email == null || fullName == null) {
            return false;
        }
        if (email.trim().length() == 0 && fullName.trim().length() == 0) {
            return false;
        }
        if (phone != null && !phone.matches(RegexHelper.PHONE_REGEX_LOCAL)) {
            return false;
        }
        return email.matches(RegexHelper.EMAIL_REGEX);
    }
}
