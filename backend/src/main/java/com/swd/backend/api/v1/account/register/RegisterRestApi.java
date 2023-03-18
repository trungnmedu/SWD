package com.swd.backend.api.v1.account.register;

import com.google.gson.Gson;
import com.swd.backend.constance.RoleProperties;
import com.swd.backend.entity.AccountEntity;
import com.swd.backend.entity.AccountOtpEntity;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.service.AccountService;
import com.swd.backend.service.OtpStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class RegisterRestApi {
    Gson gson;
    AccountService accountService;
    OtpStateService otpStateService;

    @PostMapping("register")
    @Operation(summary = "Register user by email and password.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Missing request body, request body wrong format or email is already used by another account."),
                    @ApiResponse(responseCode = "500", description = "Can't generate jwt token or access database failed.")
            }
    )
    public ResponseEntity<String> register(@RequestBody(required = false) RegisterRequest registerRequest) {
        try {
            //Case request empty body
            if (registerRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            //Case request body missing required username, password, email.
            if (!registerRequest.isValidRequest()) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Request body incorrect format").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            String error = registerRequest.checkBusinessError();
            if (error != null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message(error).build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            //Call user-service's create new user method
            AccountEntity accountEntity = accountService.createAccount(registerRequest.getEmail(), registerRequest.getFullName(), registerRequest.getPassword(), registerRequest.getPhone(), RoleProperties.ROLE_USER);
            //Call otp-service's otp generate method
            AccountOtpEntity accountOtpEntity = otpStateService.generateOtp(accountEntity.getUserId());
            //Call user-service's send mail asynchronous method
            accountService.sendOtpVerifyAccount(accountEntity, accountOtpEntity);
            return ResponseEntity.ok("Create account success!");
        } catch (DataAccessException dataAccessException) {
            ErrorResponse errorResponse = ErrorResponse.builder().message(dataAccessException.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().body("Server temp error.");
        }
    }

}
