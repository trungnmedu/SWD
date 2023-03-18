package com.swd.backend.api.v1.owner.voucher;

import com.google.gson.Gson;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.model.MessageResponse;
import com.swd.backend.model.SearchModel;
import com.swd.backend.model.VoucherModel;
import com.swd.backend.service.SecurityContextService;
import com.swd.backend.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/owners/me")
@RestController
@AllArgsConstructor
public class OwnerVoucherRestApi {
    private Gson gson;
    private VoucherService voucherService;
    private SecurityContextService securityContextService;

    @PostMapping("vouchers/create")
    public ResponseEntity<String> createVouchers(@RequestBody(required = false) VoucherModel voucherModel) {
        try {
            if (voucherModel == null) {
                ErrorResponse response = ErrorResponse.builder().message("Missing body!").build();
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(securityContext);
            voucherService.createVoucher(voucherModel, accountId);
            MessageResponse response = MessageResponse.builder().message("Created voucher success!").build();
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't create voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PutMapping("vouchers/update")
    public ResponseEntity<String> updateVoucher(@RequestBody(required = false) VoucherModel voucher) {
        try {
            if (voucher == null) {
                ErrorResponse response = ErrorResponse.builder().message("Missing body!").build();
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            voucherService.updateVoucher(voucher);
            MessageResponse messageResponse = MessageResponse.builder().message("Save change success!").build();
            return ResponseEntity.ok(gson.toJson(messageResponse));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't search voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PostMapping("vouchers")
    public ResponseEntity<String> searchAndFilterVoucher(@RequestBody(required = false) SearchModel search) {
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);
            VoucherResponse searchResult = voucherService.SearchVoucherByOwnerId(ownerId, search);
            return ResponseEntity.ok().body(gson.toJson(searchResult));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().stack(exception.getMessage()).message("Server busy temp can't search voucher.").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

}
