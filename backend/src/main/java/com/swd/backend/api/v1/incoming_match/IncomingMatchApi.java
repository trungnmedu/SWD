package com.swd.backend.api.v1.incoming_match;

import com.google.gson.Gson;
import com.swd.backend.entity.BookingEntity;
import com.swd.backend.model.MatchModel;
import com.swd.backend.service.BookingService;
import com.swd.backend.service.MatchService;
import com.swd.backend.service.SecurityContextService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/me")
@AllArgsConstructor
public class IncomingMatchApi {
    private Gson gson;
    private SecurityContextService securityContextService;
    private BookingService bookingService;
    private MatchService matchService;
    private static final int ITEMS_PER_PAGE_DEFAULT = 5;
    private static final int PAGE_DEFAULT = 1;

    @PostMapping(value = "incoming-matches")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401", description = "User is not login."),
                    @ApiResponse(responseCode = "403", description = "User is not have role 'User'. ")
            }
    )
    public ResponseEntity<String> incomingMatch(@RequestBody(required = false) IncomingRequest request) {
        IncomingResponse response;
        int itemsPerPage = ITEMS_PER_PAGE_DEFAULT;
        int page = PAGE_DEFAULT;
        if (request != null) {
            page = request.getPage() > 0 ? request.getPage() : page;
            itemsPerPage = request.getItemsPerPage() > 0 ? request.getItemsPerPage() : itemsPerPage;
        }
        try {
            String userId;
            SecurityContext context = SecurityContextHolder.getContext();
            userId = securityContextService.extractUsernameFromContext(context);

            int countAllItems = bookingService.countAllIncomingMatchesOfUser(userId);

            if (countAllItems <= 0) {
                response = IncomingResponse.builder()
                        .message("There were no result in data.")
                        .page(page)
                        .maxResult(countAllItems)
                        .data(new ArrayList<>())
                        .build();
                return ResponseEntity.ok().body(gson.toJson(response));
            }
            List<BookingEntity> incomingBookings = bookingService.getIncomingMatchesOfUser(userId, itemsPerPage, page);
            List<MatchModel> matchModels = matchService.getListMatchModelFromListBookingEntity(incomingBookings);
            response = IncomingResponse.builder()
                    .message("Get all incoming match successfully.")
                    .page(page)
                    .maxResult(countAllItems)
                    .data(matchModels)
                    .build();

            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex) {

            return ResponseEntity.internalServerError().body("Error in server: " + ex.getMessage());
        }
    }
}
