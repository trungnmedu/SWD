package com.swd.backend.model;

import com.swd.backend.api.v1.owner.yard.request.SlotRequest;
import com.swd.backend.entity.SlotEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SlotInfo {
    private String start;
    private String end;
    private int price;
    @Builder.Default
    private boolean isExistedInStorage = false;

    @Override
    public boolean equals(Object anotherSlot) {
        SlotInfo slot = (SlotInfo) anotherSlot;
        return this.start.equals(slot.start)
                && this.end.equals(slot.end)
                && this.price == slot.price;
    }

    public boolean isPriceChange(SlotInfo anotherSlot) {
        return this.start.equals(anotherSlot.getStart())
                && this.end.equals(anotherSlot.getEnd())
                && this.price != anotherSlot.getPrice();
    }

    public static SlotInfo getSlotInfo(SlotEntity slotEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return SlotInfo.builder().start(slotEntity.getStartTime().format(formatter))
                .end(slotEntity.getEndTime().format(formatter))
                .price(slotEntity.getPrice())
                .isExistedInStorage(true)
                .build();
    }

    public static SlotInfo getSlotInfo(SlotRequest slotRequest) {
        return SlotInfo.builder().start(slotRequest.getStartTime())
                .end(slotRequest.getEndTime())
                .price(slotRequest.getPrice())
                .isExistedInStorage(false)
                .build();
    }
}
