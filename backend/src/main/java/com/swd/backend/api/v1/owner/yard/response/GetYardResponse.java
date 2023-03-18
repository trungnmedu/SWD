package com.swd.backend.api.v1.owner.yard.response;

import com.swd.backend.model.YardModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetYardResponse {
    private int page;
    private int maxResult;
    private List<YardModel> listYard;
}
