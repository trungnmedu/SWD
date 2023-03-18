package com.swd.backend.api.v1.account.verifyaccount;

import com.google.gson.Gson;
import com.swd.backend.api.v1.account.login.LoginResponse;
import com.swd.backend.entity.AccountEntity;
import com.swd.backend.entity.AccountOtpEntity;
import com.swd.backend.entity.RoleEntity;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.service.*;
import com.swd.backend.utils.DateHelper;
import com.swd.backend.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class VerifyAccount {
    AccountService accountService;
    SecurityContextService securityContextService;
    Gson gson;
    OtpStateService otpStateService;
    JwtTokenUtils jwtTokenUtils;
    RoleService roleService;
    AccountLoginService accountLoginService;

    @PostMapping(value = "verify-account")
    public ResponseEntity<String> verifyAccount(@RequestBody(required = false) RequestVerify verify) {
        try {
            if (verify == null || !verify.isValid()) {
                return ResponseEntity.badRequest().body("Missing body or otp code not valid.");
            }
            //Get current authentication
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            //Get state otp of current user
            AccountOtpEntity accountOtpEntity = otpStateService.findOtpStateByUserId(userId);
            if (accountOtpEntity == null) {
                ErrorResponse error = ErrorResponse.builder().message("Otp not available.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
            if (now.after(accountOtpEntity.getExpireAt())) {
                ErrorResponse error = ErrorResponse.builder().message("Time otp expire: " + accountOtpEntity.getExpireAt()).build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            if (!accountOtpEntity.getOtpCode().matches(verify.getOtpCode()) || accountOtpEntity.isUsed()) {
                ErrorResponse error = ErrorResponse.builder().message("Otp incorrect or not lasted otp.").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            AccountEntity account = accountService.findAccountByUsername(userId);
            if (account == null) {
                ErrorResponse error = ErrorResponse.builder().message("Confirm failed!").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            if (!account.isConfirmed()) {
                account.setConfirmed(true);
                accountService.updateUser(account);
            }
            RoleEntity role = roleService.getRoleById(account.getRoleId());
            String token = jwtTokenUtils.doGenerateToken(
                    account.getUserId(),
                    account.getFullName(),
                    account.getEmail(),
                    account.getPhone(),
                    role.getRoleName(),
                    account.isConfirmed()
            );
            accountOtpEntity.setUsed(true);
            otpStateService.updateState(accountOtpEntity);
            accountLoginService.saveLogin(account.getUserId(), token);
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .userId(account.getUserId())
                    .avatar(account.getAvatar())
                    .email(account.getEmail())
                    .phone(account.getPhone())
                    .role(role.getRoleName())
                    .fullName(account.getFullName())
                    .build();
            return ResponseEntity.ok().body(gson.toJson(loginResponse));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse error = ErrorResponse.builder().message("Server busy.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(error));
        }
    }

    @GetMapping(value = "verify-account")
    public ResponseEntity<String> resendEmailVerifyAccount() {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            AccountEntity accountEntity = accountService.findAccountByUsername(userId);
            if (accountEntity == null) {
                return ResponseEntity.badRequest().body("User not exist!");
            }
            AccountOtpEntity accountOtpEntity = otpStateService.generateOtp(userId);
            accountService.sendOtpVerifyAccount(accountEntity, accountOtpEntity);
            return ResponseEntity.ok().body("Resend verify success!");
        } catch (ClassCastException | DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Server busy.");
        }
    }
}
