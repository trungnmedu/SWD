package com.swd.backend.api.v1.book.cancel_booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CancelBookingRequest {
    private String reason;

    public boolean isValid() {
        return reason != null
                && !reason.trim().isEmpty();
    }
}
