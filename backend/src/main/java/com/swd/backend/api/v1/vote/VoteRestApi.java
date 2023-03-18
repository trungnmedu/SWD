package com.swd.backend.api.v1.vote;

import com.google.gson.Gson;
import com.swd.backend.exception.ErrorResponse;
import com.swd.backend.model.SearchModel;
import com.swd.backend.service.SecurityContextService;
import com.swd.backend.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
@AllArgsConstructor
public class VoteRestApi {

    private VoteService voteService;
    private SecurityContextService securityContextService;
    private Gson gson;

    @PostMapping(value = "me/votes")
    public ResponseEntity<String> getVote(@RequestBody(required = false) SearchModel request) {
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(securityContext);
            GetVoteResponse response = voteService.getAllNonVoteByUserId(userId, request);
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy can't handle this request!").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PostMapping(value = "vote/yards/{yardId}")
    public ResponseEntity<String> getVotesOfBigYard(@PathVariable String yardId, @RequestBody(required = false) SearchModel request) {
        try {
            GetVoteResponse response;
            if (request == null) {
                response = voteService.getAllVoteByBigYardId(yardId, null, null);
            } else {
                response = voteService.getAllVoteByBigYardId(yardId, request.getItemsPerPage(), request.getPage());
            }
            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().message("Server busy can't handle this request!").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PostMapping(value = "me/vote")
    public ResponseEntity<String> postVote(@RequestBody(required = false) VoteRequest voteRequest) {
        try {
            if (voteRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("{message: \"Request failed!\"}").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            boolean postVote = voteService.postVote(
                    userId,
                    voteRequest.getBookingId(),
                    voteRequest.getScore(),
                    voteRequest.getComment()
            );

            if (postVote) {
                return ResponseEntity.ok("{\"message\": \"Vote success!\"}");
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("{message: \"Server busy can't handle this request!\"}").build();
                return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().message("{message: \"Server busy can't handle this request!\"}").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @PutMapping(value = "me/vote")
    public ResponseEntity<String> editVote(@RequestBody(required = false) VoteRequest voteRequest) {
        try {
            if (voteRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("{message: \"Request failed!\"}").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            SecurityContext context = SecurityContextHolder.getContext();
            String accountId = securityContextService.extractUsernameFromContext(context);
            boolean editVote = voteService.editVote(
                    accountId,
                    voteRequest.getVoteId(),
                    voteRequest.getScore(),
                    voteRequest.getComment()
            );

            if (editVote) {
                return ResponseEntity.ok("{\"message\": \"Edit vote success!\"}");
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("{\"message\": \"Server busy can't handle this request!\"}").build();
                return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().message("{\"message\": \"Server busy can't handle this request!\"}").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }

    @DeleteMapping(value = "me/vote")
    public ResponseEntity<String> deleteVote(@RequestBody(required = false) VoteRequest voteRequest) {
        try {
            if (voteRequest == null) {
                ErrorResponse errorResponse = ErrorResponse.builder().message("{\"message\": \"Missing body!\"}").build();
                return ResponseEntity.badRequest().body(gson.toJson(errorResponse));
            }
            SecurityContext context = SecurityContextHolder.getContext();
            String userId = securityContextService.extractUsernameFromContext(context);
            boolean deleteVoteVote = voteService.deleteVote(
                    userId,
                    voteRequest.getVoteId()
            );

            if (deleteVoteVote) {
                return ResponseEntity.ok("{\"message\": \"Deleted vote success!\"}");
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder().message("{\"message\": \"Server busy can't handle this request!\"}").build();
                return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder().message("{\"message\": \"Server busy can't handle this request!\"}").build();
            return ResponseEntity.internalServerError().body(gson.toJson(errorResponse));
        }
    }
}
