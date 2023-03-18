package com.swd.backend.api.v1.yard.search;

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
public class YardResponse {
    private List<YardModel> yards;
    private int page;
    private int maxResult;
}
