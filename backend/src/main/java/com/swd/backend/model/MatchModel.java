package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MatchModel {
    private String bookingId;
    private String startTime;
    private String endTime;
    private String date;
    private String bigYardId;
    private String subYardId;
    private String bigYardName;
    private String bigYardAddress;
    private String district;
    private String province;
    private String subYardName;
    private String type;
    private String bookAt;
    private int slotId;
    private Long bookingReference;
    private String bookingStatus;
    private int price;
}
