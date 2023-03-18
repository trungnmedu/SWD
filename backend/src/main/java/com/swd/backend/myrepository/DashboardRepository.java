package com.swd.backend.myrepository;

import com.swd.backend.constance.BookingStatus;
import com.swd.backend.constance.RoleProperties;
import com.swd.backend.model.SlotStatistic;
import com.swd.backend.model.YardStatisticModel;
import com.swd.backend.utils.DateHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DashboardRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<YardStatisticModel> getYardBookingTotalIncomeForOwner(String ownerId, Timestamp startDate, Timestamp endDate) {
        Query query = null;
        try {
            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            Timestamp todayInTimestamp = Timestamp.valueOf(today.toString() + " 00:00:00");
            LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));

            String nativeQuery;
            if (endDate.compareTo(todayInTimestamp) < 0) {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = ?1 AND (b.date >= ?2 AND b.date <= ?3) AND y.owner_id=?4" +
                        " GROUP BY b.big_yard_id, y.name";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, BookingStatus.SUCCESS);
                query.setParameter(2, startDate);
                query.setParameter(3, endDate);
                query.setParameter(4, ownerId);
            } else {
                nativeQuery = "SELECT b.big_yard_id, y.name, SUM(b.price)" +
                        " FROM booking b INNER JOIN slots s ON b.slot_id = s.id INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = ?1 AND (b.date >= ?2 AND (b.date < ?3 OR (b.date = ?3 AND s.end_time < ?4)))" +
                        " AND y.owner_id = ?5" +
                        " GROUP BY b.big_yard_id, y.name";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, BookingStatus.SUCCESS);
                query.setParameter(2, startDate);
                query.setParameter(3, today);
                query.setParameter(4, timeNow);
                query.setParameter(5, ownerId);
            }

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .totalIncome(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());
            return yardStatisticModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<YardStatisticModel> getNumberOfYardBookingsForOwner(String ownerId, Timestamp startDate, Timestamp endDate) {
        Query query = null;
        try {
            String nativeQuery = "SELECT b.big_yard_id, y.name, COUNT(*) " +
                    "FROM booking_history INNER JOIN booking b ON booking_history.booking_id = b.id " +
                    "                     INNER JOIN yards y ON b.big_yard_id = y.id " +
                    "WHERE booking_history.booking_status = ?1 " +
                    "    AND (b.date >= ?2 AND b.date <= ?3) " +
                    "    AND y.owner_id = ?4 " +
                    "GROUP BY b.big_yard_id, y.name";
            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, BookingStatus.SUCCESS);
            query.setParameter(2, startDate);
            query.setParameter(3, endDate);
            query.setParameter(4, ownerId);
            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .numberOfBookings(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());
            return yardStatisticModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<YardStatisticModel> getNumberOfYardBookingCanceledForOwner(String ownerId, Timestamp startDate, Timestamp endDate) {
        Query query = null;
        try {
            String nativeQuery;
            nativeQuery = "SELECT b.big_yard_id, y.name, COUNT(*) " +
                    "FROM booking_history INNER JOIN booking b ON booking_history.booking_id = b.id " +
                    "                     INNER JOIN yards y ON b.big_yard_id = y.id " +
                    "                     INNER JOIN accounts ON booking_history.created_by = accounts.id " +
                    "                     INNER JOIN roles r on accounts.role_id = r.id " +
                    "WHERE booking_history.booking_status = ?1" +
                    " AND y.owner_id = ?2" +
                    " AND r.role_name = ?3" +
                    " AND b.date >= ?4 AND b.date <= ?5 " +
                    "GROUP BY b.big_yard_id, y.name";
            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, BookingStatus.CANCELED);
            query.setParameter(2, ownerId);
            query.setParameter(3, RoleProperties.ROLE_USER);
            query.setParameter(4, startDate);
            query.setParameter(5, endDate);

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .numberOfBookingCanceled(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());
            return yardStatisticModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<YardStatisticModel> getNumberOfYardBookingPlayedForOwner(String ownerId, Timestamp startDate, Timestamp endDate) {
        Query query = null;
        try {
            LocalDate today = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            Timestamp todayInTimestamp = Timestamp.valueOf(today.toString() + " 00:00:00");
            LocalTime timeNow = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));

            String nativeQuery;
            if (endDate.compareTo(todayInTimestamp) < 0) {
                nativeQuery = "SELECT b.big_yard_id, y.name, COUNT(*) " +
                        "FROM booking b INNER JOIN yards y ON b.big_yard_id = y.id " +
                        " WHERE b.status = ?1 " +
                        "        AND (b.date >= ?2 AND b.date <= ?3) " +
                        "        AND y.owner_id = ?4 " +
                        "GROUP BY b.big_yard_id, y.name;";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, BookingStatus.SUCCESS);
                query.setParameter(2, startDate);
                query.setParameter(3, endDate);
                query.setParameter(4, ownerId);
            } else {
                nativeQuery = "SELECT b.big_yard_id, y.name, COUNT(*)" +
                        " FROM booking b INNER JOIN slots s ON b.slot_id = s.id INNER JOIN yards y ON b.big_yard_id = y.id" +
                        " WHERE b.status = ?1 AND (b.date >= ?2 AND (b.date < ?3 OR (b.date = ?3 AND s.end_time < ?4)))" +
                        " AND y.owner_id = ?5" +
                        " GROUP BY b.big_yard_id, y.name";
                query = entityManager.createNativeQuery(nativeQuery);
                query.setParameter(1, BookingStatus.SUCCESS);
                query.setParameter(2, startDate);
                query.setParameter(3, todayInTimestamp);
                query.setParameter(4, timeNow);
                query.setParameter(5, ownerId);
            }

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }

            List<YardStatisticModel> yardStatisticModels = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                return YardStatisticModel.builder().yardId((String) objects[0])
                        .yardName((String) objects[1])
                        .numberOfBookingPlayed(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());

            return yardStatisticModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<SlotStatistic> getNumberOfBookingInSlotForOwner(String ownerId, Timestamp startDate, Timestamp endDate) {
        Query query = null;
        try {
            String nativeQuery = "SELECT start_time, end_time, COUNT(*)" +
                    " FROM booking INNER JOIN slots ON booking.slot_id = slots.id " +
                    "            INNER JOIN yards y ON booking.big_yard_id = y.id " +
                    " WHERE booking.status = ?1" +
                    "        AND (booking.date >= ?2 AND booking.date <= ?3) " +
                    "        AND y.owner_id = ?4 " +
                    " GROUP BY slots.start_time, slots.end_time" +
                    " ORDER BY start_time";

            query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, BookingStatus.SUCCESS);
            query.setParameter(2, startDate);
            query.setParameter(3, endDate);
            query.setParameter(4, ownerId);

            List<?> queriedList = query.getResultList();
            if (queriedList == null) {
                return null;
            }

            List<SlotStatistic> slots = queriedList.stream().map(queriedObject -> {
                Object[] objects = (Object[]) queriedObject;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return SlotStatistic.builder().startTime(((Time) objects[0]).toLocalTime().format(formatter))
                        .endTime(((Time) objects[1]).toLocalTime().format(formatter))
                        .numberOfBooking(((BigInteger) objects[2]).longValue())
                        .build();
            }).collect(Collectors.toList());

            return slots;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
