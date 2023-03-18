package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SlotStatistic {
    private String startTime;
    private String endTime;
    private long numberOfBooking;
}
