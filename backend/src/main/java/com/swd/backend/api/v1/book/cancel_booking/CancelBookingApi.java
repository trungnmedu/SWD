package com.swd.backend.api.v1.book.cancel_booking;

import com.google.gson.Gson;
import com.swd.backend.exception.CancelBookingProcessException;
import com.swd.backend.service.BookingService;
import com.swd.backend.service.CancelBookingService;
import com.swd.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class CancelBookingApi {
    private SecurityContextService securityContextService;
    private BookingService bookingService;
    private CancelBookingService cancelBookingService;
    private Gson gson;

    @DeleteMapping(value = "me/bookings/{bookingId}")
    public ResponseEntity<String> cancelBookingFromUser(@RequestBody(required = false) CancelBookingRequest request, @PathVariable String bookingId) {
        CancelBookingResponse response;

        String userId;
        SecurityContext context = SecurityContextHolder.getContext();
        userId = securityContextService.extractUsernameFromContext(context);

        if (request == null || !request.isValid()) {
            response = new CancelBookingResponse(false, "The reason is required.");
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        try {
            cancelBookingService.cancelBookingFromUser(userId, bookingId, request);
            response = new CancelBookingResponse(true, "Cancel booking successfully");
            return ResponseEntity.ok(gson.toJson(response));
        } catch (CancelBookingProcessException cancelBookingProcessException) {
            response = new CancelBookingResponse(false, cancelBookingProcessException.getFilterMessage());
            return ResponseEntity.badRequest().body(gson.toJson(response));
        } catch (Exception exception) {
            response = new CancelBookingResponse(false, "Error when save in database: " + exception.getMessage());
            return ResponseEntity.internalServerError().body(gson.toJson(response));
        }
    }

    @DeleteMapping(value = "owners/me/bookings/{bookingId}")
    public ResponseEntity<String> cancelBookingFromOwner(@RequestBody(required = false) CancelBookingRequest request, @PathVariable String bookingId) {
        CancelBookingResponse response;

        String ownerId;
        SecurityContext context = SecurityContextHolder.getContext();
        ownerId = securityContextService.extractUsernameFromContext(context);

        if (request == null || !request.isValid()) {
            response = new CancelBookingResponse(false, "The reason is required.");
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        try {
            cancelBookingService.cancelBookingFromOwner(ownerId, bookingId, request);
            response = new CancelBookingResponse(true, "Cancel booking successfully");
            return ResponseEntity.ok(gson.toJson(response));
        } catch (CancelBookingProcessException cancelBookingProcessException) {
            response = new CancelBookingResponse(false, cancelBookingProcessException.getFilterMessage());
            return ResponseEntity.badRequest().body(gson.toJson(response));
        } catch (Exception exception) {
            response = new CancelBookingResponse(false, "Error when save in database: " + exception.getMessage());
            return ResponseEntity.internalServerError().body(gson.toJson(response));
        }
    }
}
