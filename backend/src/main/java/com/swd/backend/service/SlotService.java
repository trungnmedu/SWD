package com.swd.backend.service;

import com.swd.backend.constance.BookingStatus;
import com.swd.backend.entity.*;
import com.swd.backend.model.BookedSlotModel;
import com.swd.backend.model.Slot;
import com.swd.backend.model.model_builder.ListSlotBuilder;
import com.swd.backend.myrepository.SlotCustomRepository;
import com.swd.backend.repository.*;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class SlotService {
    private SlotCustomRepository slotCustomRepository;
    private SlotRepository slotRepository;
    private BookingRepository bookingRepository;
    private AccountRepository accountRepository;
    private YardRepository yardRepository;
    private SubYardRepository subYardRepository;
    private TypeYardRepository typeYardRepository;

    public List<Slot> getAllSlotInSubYardByDate(String subYardId, String date) {
        try {

            LocalDate queryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"));
            LocalDate now = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            if (queryDate.isBefore(now)) {
                return new ArrayList<>();
            }

            List<Slot> allSlots = getAllSlotsInSubYardByDate(subYardId, queryDate);

            List<Slot> bookedSlots = getBookedSlotsInSubYardByDate(subYardId, queryDate);

            updateBookedStateOfAllSlots(allSlots, bookedSlots);
            Collections.sort(allSlots);

            return allSlots;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Slot> getAllSlotInSubYardByDateFromOwner(String subYardId, String date) {
        try {
            List<SlotEntity> allSlotEntities = getAllSlotsInSubYardByFutureDate(subYardId);
            List<Slot> allSlots = ListSlotBuilder.getAvailableSlotsFromSlotEntities(allSlotEntities);
            List<?> queriedBookedSlots = slotCustomRepository.getAllBookedSlotInSubYardByFutureDate(subYardId, LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy")));
            List<Slot> bookedSlots = ListSlotBuilder.getBookedSlotsFromQueriedSlotEntities(queriedBookedSlots);
            allSlots = updateBookedStateOfAllSlots(allSlots, bookedSlots);
            Collections.sort(allSlots);

            return allSlots;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<Slot> getAllSlotsInSubYardByDate(String subYardId, LocalDate queryDate) {
        List<SlotEntity> allSlotEntities;
        allSlotEntities = DateHelper.isToday(queryDate) ? getAllSlotsInSubYardToday(subYardId) : getAllSlotsInSubYardByFutureDate(subYardId);
        return ListSlotBuilder.getAvailableSlotsFromSlotEntities(allSlotEntities);
    }

    private List<SlotEntity> getAllSlotsInSubYardByFutureDate(String subYardId) {
        return slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
    }

    private List<SlotEntity> getAllSlotsInSubYardToday(String subYardId) {
        LocalTime now = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
        return slotRepository.findSlotEntitiesByStartTimeGreaterThanAndRefYardAndActiveIsTrue(now, subYardId);
    }

    private List<Slot> getBookedSlotsInSubYardByDate(String subYardId, LocalDate queryDate) {
        List<?> queriedSlots;
        if (DateHelper.isToday(queryDate)) {
            queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardToday(subYardId);
        } else {
            queriedSlots = slotCustomRepository.getAllBookedSlotInSubYardByFutureDate(subYardId, queryDate);
        }
        return ListSlotBuilder.getBookedSlotsFromQueriedSlotEntities(queriedSlots);
    }

    private List<Slot> updateBookedStateOfAllSlots(List<Slot> allSlots, List<Slot> bookedSlots) {
        bookedSlots.forEach(slot -> {
            int pos = -1;
            for (int i = 0; i < allSlots.size(); ++i) {
                if (allSlots.get(i).getId() == slot.getId()) {
                    pos = i;
                    break;
                }
            }
            if (pos != -1) {
                allSlots.get(pos).setBooked(true);
            }
        });
        return allSlots;
    }

    @Transactional
    public void inactivateSlot(int slotId) {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setActive(false);
        slotRepository.save(slotEntity);
    }

    @Transactional
    public void reactivateSlot(int slotId) {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setActive(true);
        slotRepository.save(slotEntity);
    }

    @Transactional
    public void setIsParentActiveFalse(int slotId) {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setParentActive(false);
        slotRepository.save(slotEntity);
    }

    @Transactional
    public void setIsParentActiveTrue(int slotId) {
        SlotEntity slotEntity = slotRepository.findSlotEntityById(slotId);
        slotEntity.setParentActive(true);
        slotRepository.save(slotEntity);
    }

    public boolean isSlotAvailableFromBooking(int slotId, Timestamp timestamp) {
        LocalDate localDate = DateHelper.parseFromTimestampToLocalDate(timestamp);
        Timestamp startTime = Timestamp.valueOf(localDate.toString() + " 00:00:00");
        Timestamp endTime = Timestamp.valueOf(localDate.toString() + " 23:59:00");
        return bookingRepository.getBookingEntityBySlotIdAndStatusAndDateIsGreaterThanEqualAndDateIsLessThanEqual(slotId, BookingStatus.SUCCESS, startTime, endTime) == null;
    }

    public boolean isSlotActive(int slotId) {
        return slotRepository.findSlotEntityByIdAndActiveAndParentActive(slotId, true, true) != null;
    }

    public boolean isSlotExceedTimeToday(int slotId) {
        LocalTime localTimeToday = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
        SlotEntity slotEntity = slotRepository.findSlotEntityByIdAndStartTimeGreaterThanAndActive(slotId, localTimeToday, true);
        return slotEntity != null;
    }

    public BookedSlotModel getBookedSlotModel(int slotId, String date) {
        LocalDate queryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"));
        Timestamp queryDateInTimestamp = Timestamp.valueOf(queryDate + " 00:00:00");
        BookingEntity bookingEntity = bookingRepository.findBookingEntityBySlotIdAndStatusAndDate(slotId, BookingStatus.SUCCESS, queryDateInTimestamp);
        if (bookingEntity == null) {
            return null;
        }
        AccountEntity account = accountRepository.findUserEntityByUserId(bookingEntity.getAccountId());
        YardEntity yard = yardRepository.findYardEntitiesById(bookingEntity.getBigYardId());
        SubYardEntity subYard;
        subYard = subYardRepository.getSubYardEntitiesById(bookingEntity.getSubYardId());
        SlotEntity slot = slotRepository.findSlotEntityById(slotId);
        return BookedSlotModel.builder()
                .userId(bookingEntity.getAccountId())
                .userName(account.getFullName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .yardName(yard.getName())
                .subYardName(subYard.getName())
                .startTime(slot.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .endTime(slot.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .bookedTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(bookingEntity.getBookAt()))
                .price(Integer.toString(bookingEntity.getPrice()))
                .address(yard.getAddress())
                .typeYard(typeYardRepository.getTypeYardById(subYard.getTypeYard()).getTypeName())
                .bookingId(bookingEntity.getId())
                .build();
    }
}
