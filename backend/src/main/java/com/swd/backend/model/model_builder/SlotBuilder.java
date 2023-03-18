package com.swd.backend.model.model_builder;

import com.swd.backend.entity.SlotEntity;
import com.swd.backend.model.Slot;

import java.time.format.DateTimeFormatter;

public class SlotBuilder {
    public static Slot getBookedSlotFromSlotEntity(SlotEntity slotEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Slot slot = Slot.builder()
                .id(slotEntity.getId())
                .refSubYard(slotEntity.getRefYard())
                .price(slotEntity.getPrice())
                .startTime(slotEntity.getStartTime().format(formatter))
                .endTime(slotEntity.getEndTime().format(formatter))
                .isActive(slotEntity.isActive())
                .isBooked(true)
                .build();
        return slot;
    }

    public static Slot getAvailableSlotFromSlotEntity(SlotEntity slotEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Slot slot = Slot.builder()
                .id(slotEntity.getId())
                .refSubYard(slotEntity.getRefYard())
                .price(slotEntity.getPrice())
                .startTime(slotEntity.getStartTime().format(formatter))
                .endTime(slotEntity.getEndTime().format(formatter))
                .isActive(slotEntity.isActive())
                .isBooked(false)
                .build();
        return slot;
    }
}
