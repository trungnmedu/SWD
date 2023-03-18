package com.swd.backend.api.v1.booking_history.user;

import com.google.gson.Gson;
import com.swd.backend.api.v1.booking_history.BookingHistoryResponse;
import com.swd.backend.constance.RoleProperties;
import com.swd.backend.model.SearchModel;
import com.swd.backend.service.BookingHistoryService;
import com.swd.backend.service.SecurityContextService;
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
@RequestMapping(value = "api/v1/me")
public class BookingHistoryApi {
    private SecurityContextService securityContextService;
    private Gson gson;
    private BookingHistoryService bookingHistoryService;

    @PostMapping(value = "history-booking")
    public ResponseEntity<String> getBookingHistory(@RequestBody(required = false) SearchModel searchModel) {
        try {
            String userId;
            SecurityContext context = SecurityContextHolder.getContext();
            userId = securityContextService.extractUsernameFromContext(context);
            BookingHistoryResponse response = bookingHistoryService.searchAndFilterBookingHistory(userId, RoleProperties.ROLE_USER, searchModel);
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error in server: " + ex.getMessage());
        }
    }
}
