package com.swd.backend.api.v1.book.booking;

import com.swd.backend.entity.BookingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookingResponse {
    private String message;
    private boolean isError;
    private List<BookingEntity> bookings;
}
