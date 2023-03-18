package com.swd.backend.service;

import com.swd.backend.entity.BookingEntity;
import com.swd.backend.entity.SlotEntity;
import com.swd.backend.entity.YardEntity;
import com.swd.backend.exception.InactivateProcessException;
import com.swd.backend.myrepository.BookingCustomRepository;
import com.swd.backend.myrepository.SlotCustomRepository;
import com.swd.backend.myrepository.SubYardCustomRepository;
import com.swd.backend.repository.SlotRepository;
import com.swd.backend.repository.SubYardRepository;
import com.swd.backend.repository.YardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InactivationService {
    private SlotCustomRepository slotCustomRepository;
    private SubYardCustomRepository subYardCustomRepository;
    private BookingCustomRepository bookingCustomRepository;
    private SlotRepository slotRepository;
    private SubYardRepository subYardRepository;
    private YardRepository yardRepository;
    private CancelBookingService cancelBookingService;
    private SlotService slotService;
    private SubYardService subYardService;
    private YardService yardService;

    public static final String INACTIVE_SLOT_REASON = "The slot is disabled by owner";
    public static final String INACTIVE_SUB_YARD_REASON = "The sub yard of this slot is disabled by owner";
    public static final String INACTIVE_YARD_REASON = "The yard of this slot is disabled by owner";
    public static final String DELETE_YARD_REASON = "The yard of this slot is deleted";
    public static final String DELETE_SUB_YARD_REASON = "The sub-yard of this slot is deleted";

    @Transactional(rollbackFor = InactivateProcessException.class)
    public void inactivateSlot(String ownerId, int slotId) {
        if (!slotCustomRepository.findOwnerIdFromSlotId(slotId).equals(ownerId)) {
            throw new InactivateProcessException("The owner is not author of this slot.");
        }

        if (slotRepository.findSlotEntityByIdAndActive(slotId, false) != null) {
            throw new InactivateProcessException("The slot is already inactive.");
        }

        try {
            processInactivateSlot(ownerId, slotId, INACTIVE_SLOT_REASON);
        } catch (Exception ex) {
            throw new InactivateProcessException("Error in canceling process");
        }
    }

    public void processInactivateSlot(String ownerId, int slotId, String reason) {
        cancelAllBookingOfSlotProcess(ownerId, slotId, reason);
        slotService.inactivateSlot(slotId);
    }

    private void cancelAllBookingOfSlotProcess(String ownerId, int slotId, String reason) {
        List<BookingEntity> bookingEntities = bookingCustomRepository.getAllSuccessBookingEntitiesOfSlotInFuture(slotId);
        if (bookingEntities != null) {
            for (BookingEntity bookingEntity : bookingEntities) {
                cancelBookingService.cancelBookingProcessCreatedByOwner(bookingEntity, reason, ownerId);
                cancelBookingService.sendMailCancelToUser(bookingEntity, reason);
            }
        }
    }

    @Transactional(rollbackFor = InactivateProcessException.class)
    public void inactivateSubYard(String ownerId, String subYardId) {
        subYardFilter(ownerId, subYardId);
        try {
            processInactivateSubYard(ownerId, subYardId, INACTIVE_SUB_YARD_REASON);
        } catch (Exception ex) {
            throw new InactivateProcessException("Error in canceling process");
        }
    }

    private void subYardFilter(String ownerId, String subYardId) {
        if (!subYardCustomRepository.getOwnerIdOfSubYard(subYardId).equals(ownerId)) {
            throw new InactivateProcessException("The owner is not author of this sub-yard.");
        }
        if (subYardRepository.getSubYardEntityByIdAndActiveAndDeletedIsFalse(subYardId, true) == null) {
            throw new InactivateProcessException("The sub-yard is already inactive or deleted.");
        }
    }

    private void processInactivateSubYard(String ownerId, String subYardId, String message) {
        cancelAllBookingInSubYardAndSetParentActiveFalseForAllSlots(ownerId, subYardId, message);
        subYardService.setInactivationInfoToSubYardEntity(subYardId);
    }

    private void cancelAllBookingInSubYardAndSetParentActiveFalseForAllSlots(String ownerId, String subYardId, String message) {
        List<SlotEntity> slotEntitiesOfSubYard = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
        if (slotEntitiesOfSubYard != null) {
            for (SlotEntity slotEntity : slotEntitiesOfSubYard) {
                cancelAllBookingOfSlotProcess(ownerId, slotEntity.getId(), message);
                slotService.setIsParentActiveFalse(slotEntity.getId());
            }
        }
    }


    @Transactional(rollbackFor = InactivateProcessException.class)
    public void deleteSubYard(String ownerId, String subYardId) {
        if (!subYardCustomRepository.getOwnerIdOfSubYard(subYardId).equals(ownerId)) {
            throw new InactivateProcessException("The owner is not author of this sub-yard.");
        }

        try {
            cancelAllBookingInSubYardAndSetParentActiveFalseForAllSlots(ownerId, subYardId, DELETE_SUB_YARD_REASON);
            subYardService.setDeletedInfoToSubYardEntity(subYardId);
        } catch (Exception ex) {
            throw new InactivateProcessException(ex.getMessage());
        }
    }

    @Transactional(rollbackFor = InactivateProcessException.class)
    public void inactivateYard(String ownerId, String yardId) {
        yardFilter(ownerId, yardId);
        try {
            processInactivateYard(ownerId, yardId, INACTIVE_YARD_REASON);
        } catch (Exception ex) {
            throw new InactivateProcessException("Error when process inactivate yard.");
        }
    }

    private void yardFilter(String ownerId, String yardId) {
        YardEntity yardEntity = yardRepository.findYardEntitiesById(yardId);
        if (yardEntity == null) {
            throw new InactivateProcessException("The yard is not exist.");
        }
        if (!yardEntity.getOwnerId().equals(ownerId)) {
            throw new InactivateProcessException("The owner is not author of this yard.");
        }
        if (yardRepository.findYardEntityByIdAndActiveAndDeleted(yardId, true, false) == null) {
            throw new InactivateProcessException("The yard is already inactive or deleted.");
        }
    }

    @Transactional
    public void processInactivateYard(String ownerId, String yardId, String reason) {
        List<String> listYard = new ArrayList<>();
        listYard.add(yardId);
        List<String> subYardIdList = subYardRepository.getAllSubYardIdByListBigYardId(listYard);

        if (subYardIdList != null) {
            for (String subYardId : subYardIdList) {
                cancelAllBookingInSubYardAndSetParentActiveFalseForAllSlots(ownerId, subYardId, reason);
                subYardService.setIsParentActiveFalseForSubYard(subYardId);
            }
        }

        yardService.setIsActiveFalseForYard(yardId);
    }

    public void deleteYard(String ownerId, String yardId) {
        yardFilter(ownerId, yardId);
        try {
            processDeleteYard(ownerId, yardId);
        } catch (Exception ex) {
            throw new InactivateProcessException("Error when process inactivate yard.");
        }
    }

    private void processDeleteYard(String ownerId, String yardId) {
        List<String> listYard = new ArrayList<>();
        listYard.add(yardId);
        List<String> subYardIdList = subYardRepository.getAllSubYardIdByListBigYardId(listYard);
        if (subYardIdList != null)
            for (String subYardId : subYardIdList) {
                cancelAllBookingInSubYardAndSetParentActiveFalseForAllSlots(ownerId, subYardId, DELETE_YARD_REASON);
                subYardService.setIsParentActiveFalseForSubYard(subYardId);
            }

        yardService.setIsDeletedTrueForYard(yardId);
    }
}
