package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingHistoryModel {
    private long reference;
    private String bookingId;
    private String createdAt;
    private String createdBy;
    private String note;
    private String bookingStatus;
    private String bigYardName;
    private String subYardName;
    private String type;
    private int price;
    private String address;
    private String time;
}
