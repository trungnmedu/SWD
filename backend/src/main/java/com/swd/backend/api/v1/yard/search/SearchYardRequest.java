package com.swd.backend.api.v1.yard.search;

import lombok.Data;

@Data
public class SearchYardRequest {
    private Integer provinceId;
    private Integer districtId;
    private Integer itemsPerPage;
    private Integer page;
}
