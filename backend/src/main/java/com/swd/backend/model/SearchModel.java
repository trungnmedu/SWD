package com.swd.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchModel {
    private String keyword;
    private String sort;
    private FilterModel filter;
    private Integer itemsPerPage;
    private Integer page;
}
