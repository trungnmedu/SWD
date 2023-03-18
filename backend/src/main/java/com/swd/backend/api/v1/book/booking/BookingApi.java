package com.swd.backend.api.v1.book.booking;

import com.google.gson.Gson;
import com.swd.backend.constance.BookingStatus;
import com.swd.backend.entity.BookingEntity;
import com.swd.backend.entity.VoucherEntity;
import com.swd.backend.model.BookingModel;
import com.swd.backend.service.BookingService;
import com.swd.backend.service.SecurityContextService;
import com.swd.backend.service.VoucherService;
import com.swd.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/yards")
public class BookingApi {
    private SecurityContextService securityContextService;
    private YardService yardService;
    private Gson gson;
    private BookingService bookingService;
    private VoucherService voucherService;


    @PostMapping(value = "{yardId}/booking")
    public ResponseEntity<String> bookSlots(@RequestBody(required = false) BookingRequest request, @PathVariable String yardId) {
        BookingResponse response;
        List<BookingEntity> bookingEntities = new ArrayList<>();

        String userId;
        SecurityContext context = SecurityContextHolder.getContext();
        userId = securityContextService.extractUsernameFromContext(context);

        //Request Validation filter
        if (request == null) {
            response = new BookingResponse("Null request body", true, null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
        if (!request.isValid()) {
            response = new BookingResponse("Cannot parse request", true, null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //BigYard not available filter
        if (!yardService.isAvailableYard(yardId)) {
            response = new BookingResponse("The Yard entity of this slots is not active or deleted.", true, null);
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        //Booking process
        try {
            boolean isError = false, isAllError = true;
            boolean isNonValidVoucher = false;
            boolean isApplyVoucher = false;
            VoucherEntity voucher = null;
            if (request.getVoucherCode() != null) {
                voucher = voucherService.getValidApplyVoucherForBookingByVoucherCode(request.getVoucherCode());
                if (voucher != null) {
                    isApplyVoucher = true;
                } else {
                    isNonValidVoucher = true;
                }
            }

            for (BookingModel bookingModel : request.getBookingList()) {
                if (isApplyVoucher) {
                    bookingModel.setVoucherCode(voucher.getVoucherCode());
                }
                BookingEntity booking = bookingService.book(userId, yardId, bookingModel, isNonValidVoucher);
                bookingEntities.add(booking);
                if (booking.getStatus().equals(BookingStatus.FAILED)) {
                    isError = true;
                } else {
                    isAllError = false;
                }
            }
            if (isAllError) {
                response = new BookingResponse("All of your booking slot is error", isError, bookingEntities);
                return ResponseEntity.status(409).body(gson.toJson(response));
            } else {
                if (isApplyVoucher) {
                    voucher.setUsages(voucher.getUsages() + 1);
                    voucherService.updateUsesVoucher(voucher);
                }
            }
            response = new BookingResponse(isError ? "There were some booking slot error." : "Booking all slot successfully", isError, bookingEntities);
            return isError ? ResponseEntity.status(409).body(gson.toJson(response)) : ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error when save in database: " + ex.getMessage());
        }
    }
}
