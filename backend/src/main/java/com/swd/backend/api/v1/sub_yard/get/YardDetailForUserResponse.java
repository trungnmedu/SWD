package com.swd.backend.api.v1.sub_yard.get;

import com.swd.backend.model.YardData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YardDetailForUserResponse {
    private String message;
    private YardData data;
}
