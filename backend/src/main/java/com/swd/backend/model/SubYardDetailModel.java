package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubYardDetailModel {
    private String id;
    private String name;
    private String typeYard;
    private String createAt;
    private boolean isActive;
    private int reference;
    private List<SlotModel> slots;
}
