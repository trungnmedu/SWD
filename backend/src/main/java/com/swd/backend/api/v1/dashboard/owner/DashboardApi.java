package com.swd.backend.api.v1.dashboard.owner;

import com.google.gson.Gson;
import com.swd.backend.model.YardStatisticModel;
import com.swd.backend.service.DashboardService;
import com.swd.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping(value = "api/v1/owners/me")
@AllArgsConstructor
public class DashboardApi {
    private Gson gson;
    private DashboardService dashboardService;
    private SecurityContextService securityContextService;

    @PostMapping("dashboard")
    public ResponseEntity<String> getDashboardStatistic(@RequestBody(required = false) DashboardRequest request) {
        DashboardResponse response;
        Timestamp startDate = Timestamp.valueOf(request.getStartTime() + " 00:00:00");
        Timestamp endDate = Timestamp.valueOf(request.getEndTime() + " 00:00:00");

        SecurityContext context = SecurityContextHolder.getContext();
        String ownerId = securityContextService.extractUsernameFromContext(context);

        var yardStatistic = dashboardService.processGetAllInformationOfYardStatisticModel(ownerId, startDate, endDate);
        var numberOfBookingsByTime = dashboardService.getBookingByTimeStatistic(ownerId, startDate, endDate).values();
        long maxOfBookings = 0;
        long totalIncome = 0;
        for (YardStatisticModel yardStatisticModel : yardStatistic) {
            maxOfBookings = maxOfBookings > yardStatisticModel.getNumberOfBookings() ? maxOfBookings : yardStatisticModel.getNumberOfBookings();
            totalIncome += yardStatisticModel.getTotalIncome();
        }
        response = DashboardResponse.builder().message("Get statistic successfully")
                .yardStatistic(yardStatistic)
                .bookingStatisticByTime(numberOfBookingsByTime)
                .maxOfBooking(maxOfBookings)
                .totalIncome(totalIncome)
                .build();
        return ResponseEntity.ok().body(gson.toJson(response));
    }
}
