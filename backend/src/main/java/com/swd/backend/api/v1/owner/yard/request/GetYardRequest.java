package com.swd.backend.api.v1.owner.yard.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetYardRequest {
    private int itemsPerPage;
    private int page;
}
