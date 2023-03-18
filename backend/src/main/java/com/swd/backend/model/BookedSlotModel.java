package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class BookedSlotModel {
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String bookedTime;
    private String yardName;
    private String subYardName;
    private String startTime;
    private String endTime;
    private String price;
    private String address;
    private String typeYard;
    private String bookingId;
}
