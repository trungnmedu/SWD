package com.swd.backend.repository;

import com.swd.backend.entity.SlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<SlotEntity, Integer> {
    public List<SlotEntity> findSlotEntitiesByRefYardAndActiveIsTrue(String refYard);

    public List<SlotEntity> findSlotEntitiesByRefYard(String refYard);

    public List<SlotEntity> findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(LocalTime startTime, String refYard);

    public SlotEntity findSlotEntityByIdAndStartTimeGreaterThanAndActive(int slotId, LocalTime startTime, boolean isActive);

    public SlotEntity findSlotEntityByIdAndActive(int slotId, boolean isActive);

    public SlotEntity findSlotEntityByIdAndActiveAndParentActive(int slotId, boolean isActive, boolean isParentActive);

    @Query("SELECT slot FROM SlotEntity slot WHERE slot.refYard IN :listSubYardId")
    public List<SlotEntity> getAllSlotsByListSubYardId(@Param("listSubYardId") Collection<String> listSubYardId);

    @Query("SELECT slot FROM SlotEntity slot WHERE slot.id IN :listSlotId")
    public List<SlotEntity> getAllSlotEntityByListSlotId(@Param("listSlotId") Collection<String> listSlotId);

    @Query("SELECT slot.id FROM SlotEntity slot WHERE slot.refYard IN :listSubYardId")
    public List<Integer> getAllSlotIdsByListSubYardId(@Param("listSubYardId") Collection<String> listSubYardId);

    public SlotEntity findSlotEntityById(int slotId);

    public SlotEntity findSlotEntityByRefYardAndStartTimeAndEndTimeAndPriceAndActiveIsTrue(String refYard, LocalTime startTime, LocalTime endTime, int price);
}
