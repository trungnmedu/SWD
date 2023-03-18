package com.swd.backend.api.v1.owner.inactivation.inactivate_sub_yard;

import com.google.gson.Gson;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.exception.InactivateProcessException;
import com.swd.backend.model.MessageResponse;
import com.swd.backend.service.InactivationService;
import com.swd.backend.service.ReactivationService;
import com.swd.backend.service.SecurityContextService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/owners/me")
public class InactivateSubYardApi {
    private Gson gson;
    private ReactivationService reactivationService;
    private InactivationService inactivationService;
    private SecurityContextService securityContextService;

    @PutMapping(value = "sub-yards/{subYardId}/deactivate")
    public ResponseEntity<String> inactivateSubYard(@PathVariable(name = "subYardId") String subYardId) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            inactivationService.inactivateSubYard(ownerId, subYardId);
            MessageResponse message = new MessageResponse("Deactivate successfully.");
            return ResponseEntity.ok().body(gson.toJson(message));
        } catch (InactivateProcessException inactivateProcessException) {
            ErrorResponse response = ErrorResponse.builder().message(inactivateProcessException.getFilterMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }

    @PutMapping(value = "sub-yards/{subYardId}/activate")
    public ResponseEntity<String> reactiveSubYard(@PathVariable(name = "subYardId") String subYardId) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            reactivationService.reactiveSubYard(ownerId, subYardId);
            MessageResponse response = new MessageResponse("Activate successfully");
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            ErrorResponse response = ErrorResponse.builder().message(ex.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }

    @DeleteMapping(value = "sub-yards/{subYardId}")
    public ResponseEntity<String> deleteSubYard(@PathVariable(name = "subYardId") String subYardId) {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            String ownerId = securityContextService.extractUsernameFromContext(context);

            inactivationService.deleteSubYard(ownerId, subYardId);
            MessageResponse message = new MessageResponse("Delete successfully.");
            return ResponseEntity.ok().body(gson.toJson(message));
        } catch (InactivateProcessException ex) {
            ex.printStackTrace();
            ErrorResponse response = ErrorResponse.builder().message(ex.getMessage()).build();
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }
    }
}
