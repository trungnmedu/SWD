package com.swd.backend.api.v1.voucher;

import com.swd.backend.model.BookingApplyVoucherModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ApplyVoucherResponse {
    private String voucherId;
    private String voucherCode;
    private List<BookingApplyVoucherModel> bookingList;
}
