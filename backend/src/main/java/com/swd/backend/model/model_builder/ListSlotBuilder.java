package com.swd.backend.model.model_builder;

import com.swd.backend.entity.SlotEntity;
import com.swd.backend.model.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListSlotBuilder {
    public static List<Slot> getAvailableSlotsFromSlotEntities(List<SlotEntity> slotEntities) {
        List<Slot> slots = new ArrayList<>();
        if (slotEntities != null) {
            slotEntities.stream().forEach(slotEntity -> {
                Slot slot = SlotBuilder.getAvailableSlotFromSlotEntity(slotEntity);
                slots.add(slot);
            });
        }
        return slots;
    }

    public static List<Slot> getBookedSlotsFromSlotEntities(List<SlotEntity> slotEntities) {
        List<Slot> slots = new ArrayList<>();
        if (slotEntities != null) {
            slotEntities.stream().forEach(slotEntity -> {
                Slot slot = SlotBuilder.getBookedSlotFromSlotEntity(slotEntity);
                slots.add(slot);
            });
        }
        return slots;
    }

    public static List<Slot> getBookedSlotsFromQueriedSlotEntities(List<?> queriedSlotEntities) {
        List<Slot> slots = new ArrayList<>();
        if (queriedSlotEntities != null) {
            slots = queriedSlotEntities.stream().map(queriedSlot -> {
                SlotEntity slot = (SlotEntity) queriedSlot;
                return SlotBuilder.getBookedSlotFromSlotEntity(slot);
            }).collect(Collectors.toList());
        }
        return slots;
    }
}
