package com.swd.backend.api.v1.account.forgot;

import com.google.gson.Gson;
import com.swd.backend.entity.AccountEntity;
import com.swd.backend.entity.RoleEntity;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.service.*;
import com.swd.backend.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/forgot")
public class ForgotPasswordRestApi {
    private OtpStateService otpStateService;
    private AccountLoginService accountLoginService;
    private AccountService accountService;
    private SecurityContextService securityContextService;
    private Gson gson;
    private RoleService roleService;
    private JwtTokenUtils jwtTokenUtils;

    @PostMapping(value = "send-mail")
    public ResponseEntity<String> sendOtpRestPassword(@RequestBody(required = false) SendMailRequest sendMailRequest) {
        try {
            if (sendMailRequest == null || !sendMailRequest.isValid()) {
                ErrorResponse error = ErrorResponse.builder().message("Missing or invalid body request").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            AccountEntity account = accountService.findAccountByUsername(sendMailRequest.getEmail());
            if (account == null) {
                ErrorResponse error = ErrorResponse.builder().message("Email incorrect.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }
            otpStateService.sendEmailOtpRestPassword(account.getUserId(), sendMailRequest.getEmail());
            return ResponseEntity.ok().body("Send email reset password success!");
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse error = ErrorResponse.builder().message("Internal server error.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(error));
        }
    }

    @PostMapping(value = "confirm-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody(required = false) VerifyOtpRequest verifyOtpRequest) {
        try {
            if (verifyOtpRequest == null || !verifyOtpRequest.isValid()) {
                ErrorResponse error = ErrorResponse.builder().message("Missing body").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }
            AccountEntity account = accountService.findAccountByUsername(verifyOtpRequest.getEmail());
            if (account == null) {
                ErrorResponse error = ErrorResponse.builder().message("Email not match with any account.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }
            if (otpStateService.verifyOtp(account.getUserId(), verifyOtpRequest.getOtpCode())) {
                RoleEntity role = roleService.getRoleById(account.getRoleId());
                String token = jwtTokenUtils.doGenerateToken(
                        account.getUserId(),
                        account.getFullName(),
                        account.getEmail(),
                        account.getPhone(),
                        role.getRoleName(),
                        account.isConfirmed()
                );
                accountLoginService.saveLogin(account.getUserId(), token);
                ConfirmOtpResponse confirmOtpResponse = ConfirmOtpResponse.builder()
                        .token(token)
                        .build();
                return ResponseEntity.ok().body(gson.toJson(confirmOtpResponse));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse error = ErrorResponse.builder().message("Server busy.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(error));
        }
        ErrorResponse error = ErrorResponse.builder().message("Verify failed.").build();
        return ResponseEntity.badRequest().body(gson.toJson(error));
    }

    @PostMapping(value = "new-password")
    public ResponseEntity<String> saveNewPassword(@RequestBody(required = false) NewPasswordRequest newPasswordRequest) {
        try {
            if (newPasswordRequest == null || !newPasswordRequest.isValid()) {
                ErrorResponse error = ErrorResponse.builder().message("Missing or incorrect format body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String username = securityContextService.extractUsernameFromContext(securityContext);
            AccountEntity account = accountService.updatePassword(username, newPasswordRequest.getPassword());
            if (account == null) {
                ErrorResponse error = ErrorResponse.builder().message("Update password failed.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            } else {
                return ResponseEntity.ok().body("Reset password success!");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse error = ErrorResponse.builder().message("Server busy update password failed.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(error));
        }
    }
}
