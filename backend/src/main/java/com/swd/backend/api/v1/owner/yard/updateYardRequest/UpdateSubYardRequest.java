package com.swd.backend.api.v1.owner.yard.updateYardRequest;

import com.swd.backend.api.v1.owner.yard.request.SlotRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateSubYardRequest {
    private String id;
    private String name;
    private Integer type;
    private List<SlotRequest> slots;
}
