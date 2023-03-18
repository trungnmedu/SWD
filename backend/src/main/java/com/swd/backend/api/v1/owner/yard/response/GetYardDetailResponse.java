package com.swd.backend.api.v1.owner.yard.response;

import com.swd.backend.model.SubYardDetailModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetYardDetailResponse {
    private String id;
    private String name;
    private int districtId;
    private int provinceId;
    private String districtName;
    private String provinceName;
    private String address;
    private String openTime;
    private String closeTime;
    private String duration;
    private Integer score;
    private List<String> images;
    private List<SubYardDetailModel> subYards;
}
