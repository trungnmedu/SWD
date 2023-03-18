package com.swd.backend.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountModel {
    private String userId;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String createAt;
    private boolean isConfirmed;
    @Builder.Default
    private boolean isActive = true;
    private String role;
}
