package com.swd.backend.api.v1.sub_yard.get_by_owner;

import com.google.gson.Gson;
import com.swd.backend.entity.YardEntity;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.repository.YardRepository;
import com.swd.backend.service.SecurityContextService;
import com.swd.backend.service.SubYardService;
import com.swd.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "api/v1/owners")
public class GetSubYardDetailApi {
    private Gson gson;
    private SecurityContextService securityContextService;
    private SubYardService subYardService;
    private YardService yardService;
    private YardRepository yardRepository;

    @GetMapping(value = "/me/yards/{yardId}/sub-yards/{subYardId}")
    public ResponseEntity<String> getSubYardDetailById(@PathVariable("yardId") String yardId, @PathVariable("subYardId") String subYardId) {
        try {
            GetSubYardDetailResponse response;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(securityContext);

            response = subYardService.getSubYardDetailResponse(ownerId, yardId, subYardId);

            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (RuntimeException runtimeException) {
            ErrorResponse error = ErrorResponse.builder().message(runtimeException.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(error));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(value = "me/yards/{yardId}/sub-yards")
    public ResponseEntity<String> getSubYardsOfYard(@PathVariable String yardId) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);
            if (!yardService.getOwnerIdOfYard(yardId).equals(ownerId)) {
                ErrorResponse error = ErrorResponse.builder().message("The owner is not author of this yard").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            YardEntity yardEntity = yardRepository.findYardEntityByIdAndDeleted(yardId, false);
            if (yardEntity == null) {
                ErrorResponse error = ErrorResponse.builder().message("The yard is deleted or not existed").build();
                return ResponseEntity.badRequest().body(gson.toJson(error));
            }

            List<GetSubYardDetailResponse> subYardDetailResponses = subYardService.getAllSubYardDetailOfYard(yardId);
            GetAllSubYardResponse response = GetAllSubYardResponse.builder().message("Get sub-yards successfully")
                    .subYards(subYardDetailResponses)
                    .build();
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error in server: " + ex.getMessage());
        }
    }
}
