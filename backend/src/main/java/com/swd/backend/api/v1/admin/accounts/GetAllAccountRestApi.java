package com.swd.backend.api.v1.admin.accounts;

import com.google.gson.Gson;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.model.SearchModel;
import com.swd.backend.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "api/v1/admin")
@AllArgsConstructor
public class GetAllAccountRestApi {
    private AccountService accountService;
    private Gson gson;

    @PostMapping("all-accounts")
    public ResponseEntity<String> searchAndFilterUserAndOwner(@RequestBody(required = false) SearchModel searchModel) {
        try {
            GetAllAccountResponse response = accountService.searchAndFilterAccount(searchModel);
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't search voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
