package com.swd.backend.api.v1.owner.yard.request;

import lombok.Data;

import java.util.List;

@Data
public class YardRequest {
    private String name;
    private String address;
    private Integer districtId;
    private String openAt;
    private String closeAt;
    private String slotDuration;
    private List<SubYardRequest> subYards;
}
