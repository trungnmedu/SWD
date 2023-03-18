package com.swd.backend.api.v1.account.update;

import com.google.gson.Gson;
import com.swd.backend.api.v1.account.login.LoginResponse;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.model.AccountModel;
import com.swd.backend.service.AccountLoginService;
import com.swd.backend.service.AccountService;
import com.swd.backend.service.SecurityContextService;
import com.swd.backend.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class UpdateAccountRestApi {
    private Gson gson;
    private AccountService accountService;
    private SecurityContextService securityContextService;
    private JwtTokenUtils jwtTokenUtils;
    private AccountLoginService accountLoginService;

    @PostMapping(value = "me/update-profile")
    public ResponseEntity<String> updateAccount(@RequestParam(value = "avatar", required = false) MultipartFile file, @RequestParam("account") String data) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            AccountModel currentAccount;
            if (data != null) {
                UpdateAccountRequest accountRequest = gson.fromJson(data, UpdateAccountRequest.class);
                currentAccount = accountService.updateAccount(file, userId, accountRequest.getFullName(), accountRequest.getPhone());
            } else {
                currentAccount = accountService.updateAccount(file, userId, null, null);
            }

            if (currentAccount != null) {
                accountLoginService.deleteAllLogin(userId);
                String token = jwtTokenUtils.doGenerateToken(
                        currentAccount.getUserId(),
                        currentAccount.getFullName(),
                        currentAccount.getEmail(),
                        currentAccount.getPhone(),
                        currentAccount.getRole(),
                        currentAccount.isConfirmed()
                );
                accountLoginService.saveLogin(currentAccount.getUserId(), token);
                LoginResponse loginResponse = LoginResponse.builder()
                        .userId(currentAccount.getUserId())
                        .avatar(currentAccount.getAvatar())
                        .email(currentAccount.getEmail())
                        .phone(currentAccount.getPhone())
                        .role(currentAccount.getRole())
                        .token(token)
                        .fullName(currentAccount.getFullName())
                        .isConfirm(currentAccount.isConfirmed())
                        .build();

                return ResponseEntity.ok(gson.toJson(loginResponse));
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Update failed").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(dataIntegrityViolationException.getMessage()).message("Phone already used by another account").build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        } catch (Exception exception) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy can't handle this request.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PostMapping("me/verify-password")
    public ResponseEntity<String> verifyCurrentPassword(@RequestBody(required = false) VerifyCurrentPasswordRequest verifyCurrentPasswordRequest) {
        try {
            if (verifyCurrentPasswordRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            AccountModel currentAccount = accountService.verifyCurrentPassword(userId, verifyCurrentPasswordRequest.getPassword());

            if (currentAccount != null) {
                String token = jwtTokenUtils.doGenerateToken(
                        currentAccount.getUserId(),
                        currentAccount.getFullName(),
                        currentAccount.getEmail(),
                        currentAccount.getPhone(),
                        currentAccount.getRole(),
                        currentAccount.isConfirmed()
                );
                accountLoginService.saveLogin(currentAccount.getUserId(), token);
                VerifyCurrentPasswordResponse response = VerifyCurrentPasswordResponse.builder().message("Password correct").token(token).build();
                return ResponseEntity.ok(gson.toJson(response));
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Password isn't correct.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(e.getMessage()).message("Server busy can't handle this request.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
