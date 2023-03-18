package com.swd.backend.myrepository;

import com.swd.backend.constance.BookingStatus;
import com.swd.backend.model.VoteModel;
import com.swd.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VoteCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<VoteModel> getAllVoteByUserId(String userId) {
        String nativeQuery = "SELECT votes.id as vote_id, votes.score, votes.comment, yards.name, sub_yards.name as sub_yard_name, type_yards.type_name, yards.address, booking.date, slots.start_time, slots.end_time, booking.id as book_id FROM booking" +
                " INNER JOIN slots ON slots.id = booking.slot_id" +
                " INNER JOIN sub_yards ON slots.ref_yard = sub_yards.id" +
                " INNER JOIN yards ON sub_yards.parent_yard = yards.id" +
                " LEFT JOIN votes ON votes.booking_id = booking.id" +
                " INNER JOIN type_yards ON type_yards.id = sub_yards.type_yard" +
                " WHERE (booking.account_id = ?1) AND (booking.status = ?2) AND (booking.date < ?3)";
        Query query = entityManager.createNativeQuery(nativeQuery);
        query.setParameter(1, userId);
        query.setParameter(2, BookingStatus.SUCCESS);
        query.setParameter(3, DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        List<?> results = query.getResultList();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/y");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");
        return results.stream().map(result -> {
            Object[] vote = (Object[]) result;
            VoteModel voteModel = VoteModel.builder().build();
            voteModel.setVoteId((String) vote[0]);
            voteModel.setScore((Integer) vote[1]);
            voteModel.setComment((String) vote[2]);
            voteModel.setYardName((String) vote[3]);
            voteModel.setSubYardName((String) vote[4]);
            voteModel.setTypeName((String) vote[5]);
            voteModel.setAddress((String) vote[6]);
            voteModel.setDate(((Timestamp) vote[7]).toLocalDateTime().format(dateFormatter));
            voteModel.setStartTime(((Time) vote[8]).toLocalTime().format(timeFormatter));
            voteModel.setEndTime(((Time) vote[9]).toLocalTime().format(timeFormatter));
            voteModel.setBookingId((String) vote[10]);
            return voteModel;
        }).collect(Collectors.toList());
    }

    public List<VoteModel> getAllNonVoteByUserId(String userId, int offSet, int page) {
        try {
            Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String nativeQuery = "SELECT yards.name, sub_yards.name as sub_yard_name, type_yards.type_name, yards.address, booking.date, slots.start_time, slots.end_time, booking.id as book_id FROM booking" +
                    " INNER JOIN slots ON slots.id = booking.slot_id" +
                    " INNER JOIN sub_yards ON slots.ref_yard = sub_yards.id" +
                    " INNER JOIN yards ON sub_yards.parent_yard = yards.id" +
                    " LEFT JOIN votes ON votes.booking_id = booking.id" +
                    " INNER JOIN type_yards ON type_yards.id = sub_yards.type_yard" +
                    " WHERE (booking.account_id = ?1) AND (booking.status = ?2) AND (booking.date < ?3) AND (votes.id IS NULL ) AND (slots.start_time > ?4)";
            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, userId);
            query.setParameter(2, BookingStatus.SUCCESS);
            query.setParameter(3, now);
            query.setParameter(4, LocalTime.parse(simpleDateFormat.format(now)));
            query.setFirstResult((page - 1) * offSet);
            query.setMaxResults(offSet);
            List<?> results = query.getResultList();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/y");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("kk:mm");
            return results.stream().map(result -> {
                Object[] vote = (Object[]) result;
                VoteModel voteModel = VoteModel.builder().build();
                voteModel.setYardName((String) vote[0]);
                voteModel.setSubYardName((String) vote[1]);
                voteModel.setTypeName((String) vote[2]);
                voteModel.setAddress((String) vote[3]);
                voteModel.setDate(((Timestamp) vote[4]).toLocalDateTime().format(dateFormatter));
                voteModel.setStartTime(((Time) vote[5]).toLocalTime().format(timeFormatter));
                voteModel.setEndTime(((Time) vote[6]).toLocalTime().format(timeFormatter));
                voteModel.setBookingId((String) vote[7]);
                return voteModel;
            }).collect(Collectors.toList());
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int countAllNonVoteByUserId(String userId) {
        try {
            Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String nativeQuery = "SELECT COUNT(*) FROM booking" +
                    " INNER JOIN slots ON slots.id = booking.slot_id" +
                    " INNER JOIN sub_yards ON slots.ref_yard = sub_yards.id" +
                    " INNER JOIN yards ON sub_yards.parent_yard = yards.id" +
                    " LEFT JOIN votes ON votes.booking_id = booking.id" +
                    " INNER JOIN type_yards ON type_yards.id = sub_yards.type_yard" +
                    " WHERE (booking.account_id = ?1) AND (booking.status = ?2) AND (booking.date < ?3) AND (votes.id IS NULL ) AND (slots.start_time > ?4)";
            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, userId);
            query.setParameter(2, BookingStatus.SUCCESS);
            query.setParameter(3, DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
            query.setParameter(4, LocalTime.parse(simpleDateFormat.format(now)));
            Object results = query.getSingleResult();
            return (results instanceof BigInteger) ? ((BigInteger) results).intValue() : 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public List<VoteModel> getAllVoteByBigYard(String bigYardId, int offSet, int page) {
        try {
            String nativeQuery = "SELECT accounts.id as account_id, accounts.avatar_url, accounts.full_name, votes.id as vote_id, votes.comment, votes.date, votes.score FROM votes" +
                    " INNER JOIN booking ON booking.id = votes.booking_id" +
                    " INNER JOIN accounts ON booking.account_id = accounts.id" +
                    " WHERE (booking.big_yard_id = ?1) AND (votes.is_deleted = 'false') ORDER BY votes.date DESC";
            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, bigYardId);
            query.setMaxResults(offSet);
            query.setFirstResult((page - 1) * offSet);
            List<?> results = query.getResultList();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return results.stream().map(result -> {
                Object[] vote = (Object[]) result;
                VoteModel voteModel = VoteModel.builder().build();
                voteModel.setAccountId((String) vote[0]);
                voteModel.setAccountAvatar((String) vote[1]);
                voteModel.setAccountFullName((String) vote[2]);
                voteModel.setVoteId((String) vote[3]);
                voteModel.setComment((String) vote[4]);
                voteModel.setPostedAt(((Timestamp) vote[5]).toLocalDateTime().format(dateFormatter));
                voteModel.setScore((Integer) vote[6]);
                return voteModel;
            }).collect(Collectors.toList());
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int countAllVoteByBigYard(String bigYardId) {
        try {
            String nativeQuery = "SELECT count(*) FROM votes" +
                    " INNER JOIN booking ON booking.id = votes.booking_id" +
                    " INNER JOIN accounts ON booking.account_id = accounts.id" +
                    " WHERE (booking.big_yard_id = ?1) AND (votes.is_deleted = 'false')";
            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, bigYardId);
            Object result = query.getSingleResult();
            return (result instanceof BigInteger) ? ((BigInteger) result).intValue() : 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }
}
