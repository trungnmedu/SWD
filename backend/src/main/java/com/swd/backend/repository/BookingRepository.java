package com.swd.backend.repository;

import com.swd.backend.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, String> {
    public BookingEntity getBookingEntityBySlotIdAndStatusAndDateIsGreaterThanEqualAndDateIsLessThanEqual(int slotId, String status, Timestamp startTime, Timestamp endTime);

    public List<BookingEntity> getBookingEntitiesByAccountIdAndDateIsGreaterThanEqualAndStatusOrderByDateAsc(String userId, Timestamp date, String status);

    public List<BookingEntity> getBookingEntitiesByAccountIdOrderByBookAtDesc(String userId);

    public BookingEntity getBookingEntityById(String id);

    @Query("SELECT booking FROM BookingEntity booking WHERE booking.slotId IN :listSlotId")
    public List<BookingEntity> getListSlotExitsBookingReference(@Param("listSlotId") Collection<Integer> listSlotId);

    public List<BookingEntity> findAllByAccountIdAndStatusAndDateBefore(String accountId, String status, Timestamp date);

    public List<BookingEntity> findAllByBigYardId(String bigYardId);

    public BookingEntity findBookingEntityById(String id);

    public BookingEntity findBookingEntityBySlotIdAndStatusAndDate(int slotId, String status, Timestamp date);
}
