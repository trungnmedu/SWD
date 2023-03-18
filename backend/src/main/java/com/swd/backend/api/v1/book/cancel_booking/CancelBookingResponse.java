package com.swd.backend.api.v1.book.cancel_booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CancelBookingResponse {
    public boolean isSucess;
    public String message;
}
