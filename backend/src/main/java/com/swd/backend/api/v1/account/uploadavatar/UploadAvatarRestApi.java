package com.swd.backend.api.v1.account.uploadavatar;

import com.google.gson.Gson;
import com.swd.backend.entity.AccountEntity;
import com.swd.backend.service.AccountService;
import com.swd.backend.service.FirebaseStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "api/v1")
@AllArgsConstructor
public class UploadAvatarRestApi {

    FirebaseStoreService firebaseStoreService;
    AccountService accountService;
    Gson gson;

    @PostMapping("upload-avatar")
    @Operation(description = "Update avatar.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "500", description = "Save avatar failed."),
                    @ApiResponse(responseCode = "200", description = "Return url img.")
            }
    )
    public ResponseEntity<String> uploadAvatar(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        //Case request missing file
        if (file == null) {
            return ResponseEntity.badRequest().body("Can't get attribute 'file' MultipartFile from request, request must body type form-data.");
        }
        try {
            //Determine current user call api from spring-security context
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails instanceof UserDetails) {
                String userId = ((UserDetails) userDetails).getUsername();
                //Get details of user
                AccountEntity accountEntity = accountService.findAccountByUsername(userId);
                //Case user notfound
                if (accountEntity == null) {
                    return ResponseEntity.badRequest().body("Account not exist, can't query user on database.");
                }
                //Call FirebaseStoreService's upload file method and receive url store on firebase store
                String url = firebaseStoreService.uploadFile(file);
                //Set avatar and update on database
                accountEntity.setAvatar(url);
                accountService.updateUser(accountEntity);
                return ResponseEntity.ok().body(url);
            }
        } catch (DataAccessException dataAccessException) {
            return ResponseEntity.internalServerError().body("Access database failed. " + dataAccessException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
        return ResponseEntity.internalServerError().build();
    }

}
