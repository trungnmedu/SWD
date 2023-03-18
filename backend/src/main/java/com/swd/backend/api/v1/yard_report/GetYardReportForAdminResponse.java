package com.swd.backend.api.v1.yard_report;

import com.swd.backend.model.YardReportModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetYardReportForAdminResponse {
    private String message;
    private int page;
    private int maxResult;
    private List<YardReportModel> yardReportModels;
}
