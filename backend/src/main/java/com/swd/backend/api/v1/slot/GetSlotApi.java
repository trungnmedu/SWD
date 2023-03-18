package com.swd.backend.api.v1.slot;

import com.google.gson.Gson;
import com.swd.backend.model.BookedSlotModel;
import com.swd.backend.model.Slot;
import com.swd.backend.service.SecurityContextService;
import com.swd.backend.service.SlotService;
import com.swd.backend.service.SubYardService;
import com.swd.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1")
public class GetSlotApi {
    private SlotService slotService;
    private SubYardService subYardService;
    private YardService yardService;
    private SecurityContextService securityContextService;
    private Gson gson;

    @PostMapping(value = "sub-yards/{subYardId}/slots")
    public ResponseEntity<String> getSlotBySubYardAndDate(@PathVariable String subYardId, @RequestBody(required = false) GetSlotRequest getSlotRequest) {
        try {
            SlotResponse response;

            //Invalid Request Filter
            if (getSlotRequest == null) {
                response = new SlotResponse("Empty body", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if (!getSlotRequest.isValid()) {
                response = new SlotResponse("Request can not be parsed", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //BigYard not available filter
            String bigYardId = subYardService.getBigYardIdFromSubYard(subYardId);
            if (!yardService.isAvailableYard(bigYardId)) {
                response = new SlotResponse("The Yard entity of this sub yard is not active or deleted.", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //SubYard not available filter
            if (!subYardService.isActiveSubYard(subYardId)) {
                response = new SlotResponse("SubYard is not active", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //Successful query
            List<Slot> slots = slotService.getAllSlotInSubYardByDate(subYardId, getSlotRequest.getDate());
            response = new SlotResponse("Get slots successful", slots);
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "owners/me/sub-yards/{subYardId}/slots")
    public ResponseEntity<String> getSlotBySubYardAndDateByOwner(@PathVariable String subYardId, @RequestBody(required = false) GetSlotRequest getSlotRequest) {
        try {
            SlotResponse response;
            String userId = securityContextService.extractUsernameFromContext(SecurityContextHolder.getContext());

            //Invalid Request Filter
            if (getSlotRequest == null) {
                response = new SlotResponse("Empty body", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if (!getSlotRequest.isValid()) {
                response = new SlotResponse("Request can not be parsed", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //BigYard not available filter
            String bigYardId = subYardService.getBigYardIdFromSubYard(subYardId);
            if (!yardService.isAvailableYard(bigYardId)) {
                response = new SlotResponse("The Yard entity of this sub yard is not active or deleted.", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if (!yardService.getOwnerIdOfYard(bigYardId).equals(userId)) {
                response = new SlotResponse("The owner is not author of this yard", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //SubYard not available filter
            if (!subYardService.isActiveSubYard(subYardId)) {
                response = new SlotResponse("SubYard is not active", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //Successful query
            List<Slot> slots = slotService.getAllSlotInSubYardByDateFromOwner(subYardId, getSlotRequest.getDate());
            response = new SlotResponse("Get slots successful", slots);
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "owners/me/sub-yards/{subYardId}/slots/{slotId}")
    public ResponseEntity<String> getSlotBySubYardAndDateByOwner(@PathVariable String subYardId, @PathVariable int slotId, @RequestBody(required = false) GetSlotRequest getSlotRequest) {
        try {
            BookedSlotDetailResponse response;
            String userId = securityContextService.extractUsernameFromContext(SecurityContextHolder.getContext());

            //Invalid Request Filter
            if (getSlotRequest == null) {
                response = new BookedSlotDetailResponse("Empty body", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if (!getSlotRequest.isValid()) {
                response = new BookedSlotDetailResponse("Request can not be parsed", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //BigYard not available filter
            String bigYardId = subYardService.getBigYardIdFromSubYard(subYardId);
            if (!yardService.isAvailableYard(bigYardId)) {
                response = new BookedSlotDetailResponse("The Yard entity of this sub yard is not active or deleted.", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }
            if (!yardService.getOwnerIdOfYard(bigYardId).equals(userId)) {
                response = new BookedSlotDetailResponse("The owner is not author of this yard", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //SubYard not available filter
            if (!subYardService.isActiveSubYard(subYardId)) {
                response = new BookedSlotDetailResponse("SubYard is not active", null);
                return ResponseEntity.badRequest().body(gson.toJson(response));
            }

            //Successful query
            BookedSlotModel bookedSlotModel = slotService.getBookedSlotModel(slotId, getSlotRequest.getDate());
            if (bookedSlotModel == null) {
                response = new BookedSlotDetailResponse("There is no booking in this slot on " + getSlotRequest.getDate(), null);
                return ResponseEntity.ok().body(gson.toJson(response));
            } else {
                response = new BookedSlotDetailResponse("Get detail successfully", bookedSlotModel);
            }
            return ResponseEntity.ok().body(gson.toJson(bookedSlotModel));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
