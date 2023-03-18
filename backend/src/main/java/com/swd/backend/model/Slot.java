package com.swd.backend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Slot implements Comparable<Slot> {
    private int id;
    private String refSubYard;
    private int price;
    private String startTime;
    private String endTime;
    private boolean isActive;
    private boolean isBooked = true;

    @Override
    public int compareTo(Slot anotherSlot) {
        return this.startTime.compareTo(anotherSlot.getStartTime());
    }

}
