package com.swd.backend.service;

import com.swd.backend.constance.BookingStatus;
import com.swd.backend.entity.BookingEntity;
import com.swd.backend.entity.SlotEntity;
import com.swd.backend.entity.YardEntity;
import com.swd.backend.model.BookingModel;
import com.swd.backend.myrepository.BookingCustomRepository;
import com.swd.backend.repository.BookingRepository;
import com.swd.backend.repository.SlotRepository;
import com.swd.backend.repository.SubYardRepository;
import com.swd.backend.repository.YardRepository;
import com.swd.backend.utils.DateHelper;
import com.swd.backend.utils.PaginationHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    private SlotService slotService;
    private SubYardService subYardService;
    private BookingRepository bookingRepository;
    private BookingCustomRepository bookingCustomRepository;
    private YardRepository yardRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private BookingHistoryService bookingHistoryService;


    @Transactional
    public BookingEntity book(String userId, String yardId, BookingModel bookingModel, boolean isNonValidVoucher) {
        String errorNote = "";
        int slotId = bookingModel.getSlotId();

        if (isNonValidVoucher) {
            errorNote = "Booking failed booking use invalid voucher";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Booking date in past filter
        LocalDate bookingDate = LocalDate.parse(bookingModel.getDate(), DateTimeFormatter.ofPattern("d/M/yyyy"));
        LocalDate now = LocalDate.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
        if (bookingDate.compareTo(now) < 0) {
            errorNote = "The date of booking is in the past";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //SubYard filter
        String subYardId = bookingModel.getRefSubYard();
        if (subYardId == null) {
            errorNote = "Yard is unavailable";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }
        if (!subYardService.isActiveSubYard(subYardId)) {
            errorNote = "Yard is unavailable";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Slot Not Available Filter
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        if (!slotService.isSlotActive(slotId)) {
            errorNote = "Slot is unavailable";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }
        if (!slotService.isSlotAvailableFromBooking(slotId, timestamp)) {
            errorNote = "Slot is busy.";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        //Local Time not exceed Start Time
        Timestamp dateRequest = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        if (DateHelper.isToday(dateRequest) && !slotService.isSlotExceedTimeToday(slotId)) {
            errorNote = "Slot is started";
            return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.FAILED);
        }

        return processBooking(userId, yardId, bookingModel, errorNote, BookingStatus.SUCCESS);
    }

    @Transactional
    public BookingEntity processBooking(String userId, String yardId, BookingModel bookingModel, String errorNote, String status) {
        try {
            BookingEntity bookingEntity = saveBookingEntity(userId, yardId, bookingModel, errorNote, status);
            addInformationToBookingHistory(bookingEntity);
            if (status.equals(BookingStatus.SUCCESS)) {
                increaseNumberOfBookingsOfYard(yardId);
            }
            return bookingEntity;
        } catch (Exception ex) {
            throw new RuntimeException("Error when process booking.");
        }
    }

    private BookingEntity saveBookingEntity(String userId, String yardId, BookingModel bookingModel, String errorNote, String status) {
        Timestamp timestamp = DateHelper.parseFromStringToTimestamp(bookingModel.getDate());
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);

        String id = UUID.randomUUID().toString();

        BookingEntity bookingEntity = BookingEntity.builder()
                .id(id)
                .accountId(userId)
                .slotId(bookingModel.getSlotId())
                .date(timestamp)
                .status(status)
                .note(errorNote)
                .price(bookingModel.getPrice())
                .voucherCode(bookingModel.getVoucherCode())
                .bookAt(now)
                .bigYardId(yardId)
                .subYardId(bookingModel.getRefSubYard())
                .originalPrice(bookingModel.getOriginalPrice())
                .build();

        return bookingRepository.save(bookingEntity);
    }

    private void increaseNumberOfBookingsOfYard(String yardId) {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        int currentNumberOfBookings = yardEntity.getNumberOfBookings() == null ? 0 : yardEntity.getNumberOfBookings();
        yardEntity.setNumberOfBookings(currentNumberOfBookings + 1);
        yardRepository.save(yardEntity);
    }

    private void addInformationToBookingHistory(BookingEntity bookingEntity) {
        bookingHistoryService.saveBookingHistory(bookingEntity, bookingEntity.getNote(), bookingEntity.getAccountId());
    }

    public List<BookingEntity> getIncomingMatchesOfUser(String userId, int itemsPerPage, int page) {
        List<BookingEntity> incomingMatches = getIncomingMatches(userId);
        List<BookingEntity> result = new ArrayList<>();

        PaginationHelper paginationHelper = new PaginationHelper(itemsPerPage, incomingMatches.size());
        int startIndex = paginationHelper.getStartIndex(page);
        int endIndex = paginationHelper.getEndIndex(page);

        if (startIndex > endIndex) return result;

        for (int i = startIndex; i <= endIndex; ++i) {
            result.add(incomingMatches.get(i));
        }
        return result;
    }

    private List<BookingEntity> getIncomingMatches(String userId) {
        List<?> queriedListToday = bookingCustomRepository.getAllOrderedIncomingBookingEntitiesOfUserToday(userId);
        List<?> queriedListFutureDate = bookingCustomRepository.getAllOrderedIncomingBookingEntitiesOfUserFutureDate(userId);
        List<BookingEntity> bookingEntities = getBookingEntitiesFromQueriedList(queriedListToday);
        List<BookingEntity> bookingEntitiesFutureDate = getBookingEntitiesFromQueriedList(queriedListFutureDate);
        bookingEntities.addAll(bookingEntitiesFutureDate);
        return bookingEntities;
    }

    private List<BookingEntity> getBookingEntitiesFromQueriedList(List<?> queriedList) {
        List<BookingEntity> result = new ArrayList<>();
        if (queriedList != null) {
            result = queriedList.stream().map(queriedBooking -> (BookingEntity) queriedBooking).collect(Collectors.toList());
        }
        return result;
    }

    public int countAllIncomingMatchesOfUser(String userId) {
        return getIncomingMatches(userId).size();
    }

    public List<BookingEntity> getAllIncomeSlotByOwnerId(String ownerId) {
        List<String> listYardId = yardRepository.getAllYardIdByOwnerId(ownerId);
        List<String> listSubYardId = subYardRepository.getAllSubYardIdByListBigYardId(listYardId);
        List<SlotEntity> listSlot = slotRepository.getAllSlotsByListSubYardId(listSubYardId);
        List<Integer> listSlotId = listSlot.parallelStream().map(SlotEntity::getId).collect(Collectors.toList());
        return bookingRepository.getListSlotExitsBookingReference(listSlotId);
    }

    public BookingEntity getBookingById(String id) {
        return bookingRepository.getBookingEntityById(id);
    }
}
