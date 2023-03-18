package com.swd.backend.api.v1.owner.booking;


import com.google.gson.Gson;
import com.swd.backend.entity.BookingEntity;
import com.swd.backend.service.BookingService;
import com.swd.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/owner")
@AllArgsConstructor
public class BookingManagementRestAPI {
    private Gson gson;
    private BookingService bookingService;
    private SecurityContextService securityContextService;

    @GetMapping(value = "booking")
    public ResponseEntity<String> showAllBooking() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String ownerId = securityContextService.extractUsernameFromContext(securityContext);
        List<BookingEntity> booking = bookingService.getAllIncomeSlotByOwnerId(ownerId);
        return ResponseEntity.ok().body(gson.toJson(booking));
    }
}