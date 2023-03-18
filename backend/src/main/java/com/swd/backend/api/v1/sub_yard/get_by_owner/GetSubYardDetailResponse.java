package com.swd.backend.api.v1.sub_yard.get_by_owner;

import com.swd.backend.model.Slot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetSubYardDetailResponse {
    private String name;
    private String type;
    private boolean isActive;
    private List<Slot> slots;
}
