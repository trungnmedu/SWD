package com.swd.backend.myrepository;

import com.swd.backend.entity.BookingHistoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookingHistoryCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<BookingHistoryEntity> getAllBookingHistoryOfOwner(String ownerId) {
        try {
            String nativeQuery = "SELECT booking_history.* " +
                    "FROM booking_history INNER JOIN booking ON booking.id = booking_history.booking_id " +
                    "INNER JOIN yards y on booking.big_yard_id = y.id " +
                    "WHERE y.owner_id = ?1 " +
                    "ORDER BY booking_history.created_at DESC";

            Query query = entityManager.createNativeQuery(nativeQuery, BookingHistoryEntity.class);
            query.setParameter(1, ownerId);

            List<?> queriedList = query.getResultList();
            return queriedList.stream().map(objectQueried -> (BookingHistoryEntity) objectQueried).collect(Collectors.toList());
        } catch (Exception ex) {
            return null;
        }
    }

    public int countAllBookingHistoryOfOwner(String ownerId) {
        try {
            String nativeQuery = "SELECT count(*) " +
                    "FROM booking_history INNER JOIN booking ON booking.id = booking_history.booking_id " +
                    "INNER JOIN yards y on booking.big_yard_id = y.id " +
                    "WHERE y.owner_id = ?1";

            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, ownerId);

            return ((BigInteger) query.getSingleResult()).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    public List<BookingHistoryEntity> getAllBookingHistoryOfUser(String userId) {
        try {
            String nativeQuery = "SELECT booking_history.* " +
                    "FROM booking_history INNER JOIN booking ON booking.id = booking_history.booking_id " +
                    "WHERE booking.account_id = ?1 " +
                    "ORDER BY booking_history.created_at DESC";

            Query query = entityManager.createNativeQuery(nativeQuery, BookingHistoryEntity.class);
            query.setParameter(1, userId);

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }
            return queriedList.stream().map(objectQueried -> (BookingHistoryEntity) objectQueried).collect(Collectors.toList());
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int countAllBookingHistoryOfUser(String userId) {
        try {
            String nativeQuery = "SELECT COUNT(*) " +
                    "FROM booking_history INNER JOIN booking ON booking_history.booking_id = booking.id " +
                    "WHERE booking.account_id = ?1";

            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, userId);
            return ((BigInteger) query.getSingleResult()).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }
}
