package com.swd.backend.model;

import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingModel {
    protected int slotId;
    protected String refSubYard;
    protected Integer price;
    protected Integer originalPrice;
    protected String voucherCode;
    protected String date;

    public boolean isValid() {
        return DateHelper.parseFromStringToTimestamp(date) != null && price >= 0 && originalPrice >= 0;
    }
}
