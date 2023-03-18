package com.swd.backend.api.v1.owner.yard.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateYardSuccessResponse {
    private String message;
}
