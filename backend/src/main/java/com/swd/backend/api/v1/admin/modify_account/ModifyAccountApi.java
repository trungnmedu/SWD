package com.swd.backend.api.v1.admin.modify_account;

import com.google.gson.Gson;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.service.AccountService;
import com.swd.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/admin/accounts")
public class ModifyAccountApi {
    private Gson gson;
    private AccountService accountService;
    private SecurityContextService securityContextService;


    @PutMapping(value = "{accountId}")
    public ResponseEntity<String> modifyAccount(@RequestBody(required = false) ModifyAccountRequest request, @PathVariable String accountId) {
        try {
            //Case request empty body
            if (request == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            //Case request body missing required username, password, email.
            if (!request.isValid()) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Request body incorrect format").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }

            SecurityContext context = SecurityContextHolder.getContext();
            String adminId = securityContextService.extractUsernameFromContext(context);

            if (adminId.equals(accountId) && request.getIsActive() == false) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Can't disable account itself.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            accountService.modifyUserInformation(accountId, request.getFullName(), request.getPhone(), request.getIsActive());
            ModifyAccountResponse response = new ModifyAccountResponse("Update user information successfully.");
            return ResponseEntity.ok(gson.toJson(response));
        } catch (DataAccessException dataAccessException) {
            if (dataAccessException.getMessage().contains("accounts_phone_key")) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Update failed. The phone is already used.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            ErrorResponse errorResponse = ErrorResponse.builder().message(dataAccessException.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        } catch (RuntimeException exception) {
            ErrorResponse errorResponse = ErrorResponse.builder().message(exception.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().body("Server temp error.");
        }
    }
}
