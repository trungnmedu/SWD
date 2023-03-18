package com.swd.backend.api.v1.sub_yard.get_by_owner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GetAllSubYardResponse {
    private String message;
    private List<GetSubYardDetailResponse> subYards;
}
