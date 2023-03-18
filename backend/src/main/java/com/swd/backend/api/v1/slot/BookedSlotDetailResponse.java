package com.swd.backend.api.v1.slot;

import com.swd.backend.model.BookedSlotModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookedSlotDetailResponse {
    private String message;
    private BookedSlotModel data;
}
