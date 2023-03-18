package com.swd.backend.service;

import com.swd.backend.api.v1.vote.GetVoteResponse;
import com.swd.backend.entity.BookingEntity;
import com.swd.backend.entity.VoteEntity;
import com.swd.backend.entity.YardEntity;
import com.swd.backend.model.SearchModel;
import com.swd.backend.model.VoteModel;
import com.swd.backend.myrepository.VoteCustomRepository;
import com.swd.backend.repository.BookingRepository;
import com.swd.backend.repository.VoteRepository;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final YardService yardService;
    private final BookingRepository bookingRepository;
    private VoteCustomRepository voteCustomRepository;

    public boolean postVote(String accountId, String bookingId, Integer score, String comment) throws DataAccessException {
        try {
            BookingEntity booking = bookingRepository.getBookingEntityById(bookingId);
            if (!booking.getAccountId().equals(accountId)) {
                return false;
            }
            VoteEntity voteEntity = VoteEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .comment(comment)
                    .score(score)
                    .bookingId(bookingId)
                    .date(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                    .build();
            voteRepository.save(voteEntity);
            reUpdateAverageScoreVote(bookingId);
            return true;
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return false;
        }
    }

    public boolean editVote(String accountId, String votedId, int score, String comment) {
        try {
            VoteEntity vote = voteRepository.findVoteEntityById(votedId);
            BookingEntity booking = bookingRepository.findBookingEntityById(vote.getBookingId());
            if (!accountId.equals(booking.getAccountId())) {
                return false;
            }
            boolean recalculateScore = false;
            if (vote.getScore() != score) {
                vote.setScore(score);
                recalculateScore = true;
            }
            vote.setComment(comment);
            voteRepository.save(vote);
            if (recalculateScore) {
                reUpdateAverageScoreVote(booking.getId());
            }
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean deleteVote(String accountId, String voteId) {
        try {
            VoteEntity vote = voteRepository.findVoteEntityById(voteId);
            BookingEntity booking = bookingRepository.findBookingEntityById(vote.getBookingId());
            if (!accountId.equals(booking.getAccountId())) {
                return false;
            }
            vote.setDeleted(true);
            voteRepository.save(vote);
            reUpdateAverageScoreVote(booking.getId());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private void reUpdateAverageScoreVote(String bookingId) {
        new Thread(() -> {
            try {
                BookingEntity booking = bookingRepository.findBookingEntityById(bookingId);
                YardEntity yard = yardService.getYardById(booking.getBigYardId());
                List<BookingEntity> bookingListOfBigYard = bookingRepository.findAllByBigYardId(yard.getId());
                List<String> listBookingId = bookingListOfBigYard.parallelStream().map(BookingEntity::getId).collect(Collectors.toList());
                List<VoteEntity> votes = voteRepository.findAllByBookingIdInAndDeletedFalse(listBookingId);

                if (votes == null) {
                    return;
                }
                float sumScore = votes.stream().reduce(0, (preSum, vote) -> preSum + vote.getScore(), Integer::sum);
                int average = Math.round(sumScore / votes.size());
                yard.setScore(average);
                yard.setNumberOfVote(votes.size());
                yardService.updateYard(yard);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public GetVoteResponse getAllNonVoteByUserId(String userId, SearchModel request) {
        int maxResult = countAllNonVote(userId);
        int offSet = 6;
        int page = 1;
        if (request != null) {
            offSet = (request.getItemsPerPage() != null) ? (int) request.getItemsPerPage() : offSet;
            page = (request.getPage() != null) ? (int) request.getPage() : page;
        }
        List<VoteModel> votes = voteCustomRepository.getAllNonVoteByUserId(userId, offSet, page);
        return GetVoteResponse.builder().votes(votes).maxResult(maxResult).page(page).build();
    }

    private List<VoteModel> getAllNonVote(String userId, int offSet, int page) {
        return voteCustomRepository.getAllNonVoteByUserId(userId, offSet, page);
    }

    public int countAllNonVote(String userId) {
        return voteCustomRepository.countAllNonVoteByUserId(userId);
    }

    private List<VoteModel> getAllVoteByBigYardId(String bigYardId, int offSet, int page) {
        return voteCustomRepository.getAllVoteByBigYard(bigYardId, offSet, page);
    }

    public GetVoteResponse getAllVoteByBigYardId(String bigYardId, Integer offSet, Integer page) {
        int maxResult = voteCustomRepository.countAllVoteByBigYard(bigYardId);
        int offSetValue = offSet != null ? (int) offSet : 10;
        int pageValue = page != null ? (int) page : 1;
        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        List<VoteModel> votes = getAllVoteByBigYardId(bigYardId, offSetValue, pageValue);
        return GetVoteResponse.builder().votes(votes).page(pageValue).maxResult(maxResult).build();
    }

}
