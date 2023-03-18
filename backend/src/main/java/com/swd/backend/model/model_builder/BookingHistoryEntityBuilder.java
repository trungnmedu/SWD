package com.swd.backend.model.model_builder;

import com.swd.backend.entity.BookingEntity;
import com.swd.backend.entity.BookingHistoryEntity;
import com.swd.backend.utils.DateHelper;

import java.util.UUID;

public class BookingHistoryEntityBuilder {
    public static BookingHistoryEntity buildFromBookingEntity(BookingEntity bookingEntity, String reason) {
        String id = UUID.randomUUID().toString();
        BookingHistoryEntity bookingHistoryEntity = BookingHistoryEntity.builder()
                .bookingId(bookingEntity.getId())
                .createdBy(bookingEntity.getAccountId())
                .createdAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                .bookingStatus(bookingEntity.getStatus())
                .id(id)
                .note(reason)
                .build();
        return bookingHistoryEntity;
    }
}
