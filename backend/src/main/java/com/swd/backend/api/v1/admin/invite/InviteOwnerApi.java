package com.swd.backend.api.v1.admin.invite;

import com.google.gson.Gson;
import com.swd.backend.entity.AccountEntity;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.service.AccountService;
import com.swd.backend.utils.PasswordGenerator;
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
@RequestMapping(value = "api/v1/admin")
@AllArgsConstructor
public class InviteOwnerApi {
    private Gson gson;
    private AccountService accountService;

    @PostMapping("owner-register")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Missing request body, request body wrong format or email is already used by another account."),
                    @ApiResponse(responseCode = "500", description = "Can't generate jwt token or access database failed.")
            }
    )
    public ResponseEntity<String> register(@RequestBody(required = false) InviteOwnerRequest inviteRequest) {
        try {
            if (inviteRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("Missing body.").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            if (!inviteRequest.isValid()) {
                return ResponseEntity.badRequest().body("Body is in incorrect format.");
            }

            int numOfCharacters = 15;
            String password = PasswordGenerator.generatePassword(numOfCharacters);

            AccountEntity accountEntity = accountService.createOwnerAccount(inviteRequest.getEmail(), inviteRequest.getFullName(), password, inviteRequest.getPhone());

            accountService.sendOwnerAccountViaEmail(accountEntity.getEmail(), password);

            InviteOwnerResponse response = InviteOwnerResponse.builder()
                    .message("Invite admin success")
                    .email(accountEntity.getEmail())
                    .phone(accountEntity.getPhone())
                    .fullName(accountEntity.getFullName())
                    .build();

            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (DataAccessException dataAccessException) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(dataAccessException.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().body("Server temp error.");
        }
    }
}
