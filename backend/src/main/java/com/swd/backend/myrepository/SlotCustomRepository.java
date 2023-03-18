package com.swd.backend.myrepository;

import com.swd.backend.constance.BookingStatus;
import com.swd.backend.entity.SlotEntity;
import com.swd.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class SlotCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getAllBookedSlotInSubYardToday(String subYardId) {
        Query query = null;
        try {
            LocalDate today = LocalDate.now();
            Timestamp startTime = Timestamp.valueOf(today.toString() + " 00:00:00");
            Timestamp endTime = Timestamp.valueOf(today.toString() + " 23:59:59");
            LocalTime localTimeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));

            String nativeQuery = "SELECT * FROM slots" +
                    " WHERE (slots.id IN (SELECT slot_id FROM booking WHERE date BETWEEN ?1 AND ?2 AND status = ?3))" +
                    " AND ref_yard = ?4" +
                    " AND is_active = true" +
                    " AND start_time > ?5";

            query = entityManager.createNativeQuery(nativeQuery, SlotEntity.class);
            query.setParameter(1, startTime);
            query.setParameter(2, endTime);
            query.setParameter(3, BookingStatus.SUCCESS);
            query.setParameter(4, subYardId);
            query.setParameter(5, localTimeNow);

            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<?> getAllBookedSlotInSubYardByFutureDate(String subYardId, LocalDate queryDate) {
        Query query = null;
        try {
            Timestamp startTime = Timestamp.valueOf(queryDate.toString() + " 00:00:00");
            Timestamp endTime = Timestamp.valueOf(queryDate.toString() + " 23:59:59");
            String nativeQuery = "SELECT * FROM slots" +
                    " WHERE (slots.id IN (SELECT slot_id FROM booking WHERE date BETWEEN ?1 AND ?2 AND status = ?3))" +
                    " AND ref_yard = ?4" +
                    " AND is_active = true";
            query = entityManager.createNativeQuery(nativeQuery, SlotEntity.class);
            query.setParameter(1, startTime);
            query.setParameter(2, endTime);
            query.setParameter(3, BookingStatus.SUCCESS);
            query.setParameter(4, subYardId);
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public String findYardIdFromSlotId(int slotId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT parent_yard" +
                    " FROM sub_yards" +
                    " WHERE id = (SELECT ref_yard FROM slots WHERE id = ?1)";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    public String findSubYardIdFromSlotId(int slotId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT id" +
                    " FROM sub_yards" +
                    " WHERE id = (SELECT ref_yard FROM slots WHERE id = ?1)";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    public String findOwnerIdFromSlotId(int slotId) {
        try {
            Query query = null;

            String nativeQuery = "SELECT y.owner_id FROM yards y INNER JOIN sub_yards sub ON y.id = sub.parent_yard " +
                    "INNER JOIN slots ON slots.ref_yard = sub.id " +
                    "WHERE slots.id = ?1";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, slotId);
            return (String) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }
}
