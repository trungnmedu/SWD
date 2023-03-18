package com.swd.backend.service;

import com.swd.backend.api.v1.book.cancel_booking.CancelBookingRequest;
import com.swd.backend.constance.BookingStatus;
import com.swd.backend.entity.*;
import com.swd.backend.exception.CancelBookingProcessException;
import com.swd.backend.model.MatchModel;
import com.swd.backend.myrepository.SlotCustomRepository;
import com.swd.backend.repository.*;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class CancelBookingService {
    private YardService yardService;
    private MatchService matchService;
    private BookingHistoryService bookingHistoryService;
    private SubYardRepository subYardRepository;
    private SlotRepository slotRepository;
    private BookingRepository bookingRepository;
    private EmailService emailService;
    private AccountRepository accountRepository;
    private YardRepository yardRepository;
    private SlotCustomRepository slotCustomRepository;
    public static final int PREVENT_CANCEL_BOOKING_IN_MINUTE = 0;

    @Transactional
    public void cancelBookingFromUser(String userId, String bookingId, CancelBookingRequest request) {
        BookingEntity booking = getBookingEntity(bookingId);
        int slotId = booking.getSlotId();
        SlotEntity slot = slotIdIsActiveFilter(slotId);
        bookingIsOfUserFilter(booking, userId);
        bookingStatusIsSuccessFilter(booking);
        slotTimeStartIsNotOverPreventTimeForCancel(booking, slot);

        cancelBookingProcess(booking, request.getReason());
    }

    private void slotTimeStartIsNotOverPreventTimeForCancel(BookingEntity booking, SlotEntity slot) {
        if (DateHelper.isToday(booking.getDate())) {
            LocalTime slotStartTime = slot.getStartTime();
            LocalTime now = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            now = now.plusMinutes(PREVENT_CANCEL_BOOKING_IN_MINUTE);
            if (now.isAfter(slotStartTime)) {
                throw new CancelBookingProcessException("The match is started or will be start now. You cannot cancel that.");
            }
        }
    }

    private BookingEntity getBookingEntity(String bookingId) {
        BookingEntity booking = bookingRepository.getBookingEntityById(bookingId);
        if (booking == null) {
            throw new CancelBookingProcessException("Can not get booking entity from bookingId");
        }
        return booking;
    }

    private void bookingIsOfUserFilter(BookingEntity booking, String userId) {
        if (!booking.getAccountId().equals(userId)) {
            throw new CancelBookingProcessException("The user is not the author this booking entity.");
        }
        return;
    }

    private void bookingStatusIsSuccessFilter(BookingEntity booking) {
        if (!booking.getStatus().equals(BookingStatus.SUCCESS)) {
            throw new CancelBookingProcessException("The booking entity of the request is not a success booking.");
        }
        return;
    }

    private void yardIsActiveAndNotDeleted(String yardId) {
        if (!yardService.isAvailableYard(yardId)) {
            throw new CancelBookingProcessException("Your booking is canceled before, the yard is inactivated or deleted. Or yard is not found.");
        }
        return;
    }

    private SubYardEntity subYardIsActiveFilter(String subYardId) {
        SubYardEntity subYard = subYardRepository.getSubYardEntityByIdAndActiveAndDeletedIsFalse(subYardId, true);
        if (subYard == null) {
            throw new CancelBookingProcessException("Your booking is canceled before, the sub-yard is inactivated by the owner. Or sub-yard not found.");
        }
        return subYard;
    }

    private SlotEntity slotIdIsActiveFilter(int slotId) {
        SlotEntity slot = slotRepository.findSlotEntityByIdAndActive(slotId, true);
        if (slot == null) {
            throw new CancelBookingProcessException("Your booking is canceled before because the slot is inactivated by owner. Or slot not found.");
        }
        return slot;
    }

    @Transactional(rollbackFor = CancelBookingProcessException.class)
    public void cancelBookingProcess(BookingEntity booking, String reason) {
        BookingEntity bookingEntity = saveBookingCanceledInformation(booking, reason);
        String yardId = slotCustomRepository.findYardIdFromSlotId(booking.getSlotId());
        decreaseNumberOfBookingsOfYard(yardId);
        saveBookingHistory(bookingEntity, reason, bookingEntity.getAccountId());
    }

    @Transactional(rollbackFor = CancelBookingProcessException.class)
    public void cancelBookingProcessCreatedByOwner(BookingEntity booking, String reason, String ownerId) {
        BookingEntity bookingEntity = saveBookingCanceledInformation(booking, reason);
        String yardId = slotCustomRepository.findYardIdFromSlotId(booking.getSlotId());
        decreaseNumberOfBookingsOfYard(yardId);
        saveBookingHistory(bookingEntity, reason, ownerId);
    }

    private BookingEntity saveBookingCanceledInformation(BookingEntity booking, String reason) {
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        booking.setStatus(BookingStatus.CANCELED);
        booking.setNote("Booking canceled at: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(now) + " - Reason: " + reason);
        bookingRepository.save(booking);
        return booking;
    }

    private void decreaseNumberOfBookingsOfYard(String yardId) {
        try {
            YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
            int currentNumberOfBookings = yardEntity.getNumberOfBookings() == null ? 0 : yardEntity.getNumberOfBookings();
            yardEntity.setNumberOfBookings(currentNumberOfBookings - 1);
            yardRepository.save(yardEntity);
        } catch (Exception ex) {
            throw new CancelBookingProcessException("Increase number of booking in yard entity failed.");
        }
    }

    private void saveBookingHistory(BookingEntity bookingEntity, String reason, String createdBy) {
        try {
            bookingHistoryService.saveBookingHistory(bookingEntity, reason, createdBy);
        } catch (Exception ex) {
            throw new CancelBookingProcessException("Cancel successfully. However, save to booking history failed due to internal error");
        }
    }

    private void sendMailToOwner(String userId,
                                 String ownerId,
                                 String yardId,
                                 SubYardEntity subyard,
                                 BookingEntity booking,
                                 SlotEntity slot,
                                 String reason) {
        AccountEntity user = accountRepository.findUserEntityByUserId(userId);
        String destination = accountRepository.findUserEntityByUserId(ownerId).getEmail();
        String htmlTemplate = getHtmlTemplate(yardService.getYardFullAddress(yardId),
                user.getFullName(),
                subyard.getName() + " - " + subyard.getTypeYard(),
                reason,
                booking,
                slot);
        emailService.sendHtmlTemplateMessage(destination, "Cancel booking from " + user.getFullName(), htmlTemplate);
    }

    public void sendMailCancelToUser(BookingEntity booking, String reason) {
        AccountEntity user = accountRepository.findUserEntityByUserId(booking.getAccountId());
        String destination = user.getEmail();
        MatchModel match = matchService.transformMatchModelFromBookingEntity(booking);
        String reasonForUser = reason;
        if (reason.equals(InactivationService.INACTIVE_SUB_YARD_REASON)
                || reason.equals(InactivationService.INACTIVE_YARD_REASON)
                || reason.equals(InactivationService.DELETE_SUB_YARD_REASON)
                || reason.equals(InactivationService.DELETE_YARD_REASON)) {
            reasonForUser = "The yard is not available";
        } else if (reason.equals(InactivationService.INACTIVE_SLOT_REASON)) {
            reasonForUser = "The slot is not available";
        }
        emailService.sendHtmlTemplateMessage(destination, "Your booking is canceled", getHtmlTemplateMailToUser(match, reasonForUser));
    }

    private static String getHtmlTemplate(String yardAddress,
                                          String userName,
                                          String subYardName,
                                          String reason,
                                          BookingEntity booking,
                                          SlotEntity slot) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        String result = "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:20px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                "<hr>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; padding: 2px; text-align: center;\">There was a booking canceled by user <span style=\"color: green ; font-style: italic;\">" + userName + "</span> </p>" +
                "<table border=\"1\" style=\"margin: 0 auto; font-size:18px;\">" +
                "<tr>" +
                "<td>Yard Address</td>" +
                "<td>" + yardAddress + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>SubYard</td>" +
                "<td>" + subYardName + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Start Time</td>" +
                "<td>" + formatter.format(slot.getStartTime()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>End Time</td>" +
                "<td>" + formatter.format(slot.getEndTime()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Date</td>" +
                "<td>" + new SimpleDateFormat("dd/MM/yyyy").format(booking.getDate()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Reason</td>" +
                "<td>" + reason + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Booking Note</td>" +
                "<td>" + booking.getNote() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Canceled At</td>" +
                "<td>" + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(booking.getBookAt()) + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Status</td>" +
                "<td> CANCELED </td>" +
                "</tr>" +
                "</table>" +
                "<p style=\"text-align: center;\">--------------</p>";
        return result;
    }

    private static String getHtmlTemplateMailToUser(MatchModel matchModel, String reason) {
        String result = "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:20px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                "<hr>" +
                "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; padding: 2px; text-align: center;\">There was a booking canceled due to the reason: " + reason + ". Your booking detail: " +
                "<table border=\"1\" style=\"margin: 0 auto; font-size:18px\">" +
                "<tr>" +
                "<td>Address</td>" +
                "<td>" + matchModel.getBigYardAddress() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Yard</td>" +
                "<td>" + matchModel.getBigYardName() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>SubYard</td>" +
                "<td>" + matchModel.getSubYardName() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Type</td>" +
                "<td>" + matchModel.getType() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Start Time</td>" +
                "<td>" + matchModel.getStartTime() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>End Time</td>" +
                "<td>" + matchModel.getEndTime() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Date</td>" +
                "<td>" + matchModel.getDate() + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Canceled At</td>" +
                "<td>" + new SimpleDateFormat("dd/MM/yyyy").format(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE)) + "</td>" +
                "</tr>" +
                "</table>" +
                "<p style=\"text-align: center;\">--------------</p>";
        return result;
    }

    @Transactional
    public void cancelBookingFromOwner(String ownerId, String bookingId, CancelBookingRequest request) {
        BookingEntity booking = getBookingEntity(bookingId);

        int slotId = booking.getSlotId();
        SlotEntity slot = slotRepository.findSlotEntityById(slotId);

        bookingIsInOwnerYardFilter(booking, ownerId);
        bookingStatusIsSuccessFilter(booking);
        slotTimeStartIsNotOverPreventTimeForCancel(booking, slot);

        cancelBookingProcessCreatedByOwner(booking, request.getReason(), ownerId);
        sendMailCancelToUser(booking, request.getReason());
    }

    private void bookingIsInOwnerYardFilter(BookingEntity booking, String ownerId) {
        YardEntity yard = yardRepository.findYardEntitiesById(booking.getBigYardId());
        if (yard == null || !yard.getOwnerId().equals(ownerId)) {
            throw new CancelBookingProcessException("The booking is not in owner yard.");
        }
    }
}
