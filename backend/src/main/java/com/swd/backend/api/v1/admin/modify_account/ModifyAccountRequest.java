package com.swd.backend.api.v1.admin.modify_account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swd.backend.utils.RegexHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyAccountRequest {
    private String fullName;
    private String phone;

    @JsonProperty("isActive")
    private Boolean isActive;

    public boolean isValid() {
        if ((fullName != null && fullName.trim().equals("")) || (phone != null && !phone.trim().equals("") && !phone.matches(RegexHelper.PHONE_REGEX_LOCAL))) {
            return false;
        }
        return true;
    }
}
