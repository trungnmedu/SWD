package com.swd.backend.api.v1.owner.yard.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubYardRequest {
    private String name;
    private Integer type;
    private List<SlotRequest> slots;
}
