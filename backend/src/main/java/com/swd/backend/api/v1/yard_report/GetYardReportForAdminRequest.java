package com.swd.backend.api.v1.yard_report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetYardReportForAdminRequest {
    private int page;
    private int itemsPerPage;
}
