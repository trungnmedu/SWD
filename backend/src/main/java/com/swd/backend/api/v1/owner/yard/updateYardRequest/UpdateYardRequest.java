package com.swd.backend.api.v1.owner.yard.updateYardRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateYardRequest {
    private String name;
    private String address;
    private Integer districtId;
    private String openAt;
    private String closeAt;
    private String slotDuration;
    private List<UpdateSubYardRequest> subYards;
}
