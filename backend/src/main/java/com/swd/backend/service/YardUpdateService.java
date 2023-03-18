package com.swd.backend.service;

import com.swd.backend.api.v1.owner.yard.request.SlotRequest;
import com.swd.backend.api.v1.owner.yard.request.SubYardRequest;
import com.swd.backend.api.v1.owner.yard.updateYardRequest.UpdateSubYardRequest;
import com.swd.backend.api.v1.owner.yard.updateYardRequest.UpdateYardRequest;
import com.swd.backend.entity.*;
import com.swd.backend.model.SlotInfo;
import com.swd.backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class YardUpdateService {
    private YardRepository yardRepository;
    private YardPictureRepository yardPictureRepository;
    private SubYardRepository subYardRepository;
    private TypeYardRepository typeYardRepository;
    private SlotRepository slotRepository;
    private YardService yardService;
    private SubYardService subYardService;
    private FirebaseStoreService firebaseStoreService;
    private InactivationService inactivationService;


    @Transactional(rollbackFor = RuntimeException.class)
    public void updateYard(String ownerId,
                           UpdateYardRequest updateYardRequest,
                           List<String> images,
                           MultipartFile[] newImages,
                           String yardId) {
        YardEntity yard = yardRepository.findYardEntitiesById(yardId);

        if (!ownerId.equals(yard.getOwnerId())) {
            throw new RuntimeException("Owner is not author of this yard");
        }

        try {
            updateImages(images, newImages, yardId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            yard.setAddress(updateYardRequest.getAddress());
            yard.setDistrictId(updateYardRequest.getDistrictId());
            yard.setName(updateYardRequest.getName());
            yard.setOpenAt(LocalTime.parse(updateYardRequest.getOpenAt(), formatter));
            yard.setCloseAt(LocalTime.parse(updateYardRequest.getCloseAt(), formatter));
            yard.setSlotDuration(getSlotDuration(updateYardRequest.getSlotDuration()));

            List<UpdateSubYardRequest> subYards = updateYardRequest.getSubYards();
            List<SubYardRequest> subYardToAdd = new ArrayList<>();
            List<UpdateSubYardRequest> subYardToUpdate = new ArrayList<>();
            for (UpdateSubYardRequest subYardRequest : subYards) {
                if (subYardRequest.getId() == null || subYardRequest.getId().equals("")) {
                    subYardToAdd.add(new SubYardRequest(subYardRequest.getName(), subYardRequest.getType(), subYardRequest.getSlots()));
                } else {
                    subYardToUpdate.add(subYardRequest);
                }
            }
            yardService.addSubYard(subYardToAdd, yardId);
            updateSubYards(ownerId, subYardToUpdate);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void updateImages(List<String> images, MultipartFile[] newImages, String yardId) {
        if (images == null || images.size() == 0) {
            return;
        }

        if (images.size() != newImages.length) {
            throw new RuntimeException("Image to update not match the current image request");
        }

        try {
            for (int i = 0; i < images.size(); ++i) {
                String currentImgUrl = images.get(i);
                String currentImgName = currentImgUrl.split("[/?]")[7];
                firebaseStoreService.deleteFile(currentImgName);
                YardPictureEntity picture = yardPictureRepository.findTop1ByRefIdAndImage(yardId, currentImgUrl);
                picture.setImage(firebaseStoreService.uploadFile(newImages[i]));
                yardPictureRepository.save(picture);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void updateSubYards(String ownerId, List<UpdateSubYardRequest> subYardRequests) {
        if (subYardRequests != null && subYardRequests.size() > 0) {
            for (UpdateSubYardRequest request : subYardRequests) {
                SubYardEntity subYardEntity = subYardService.getSubYardById(request.getId());

                subYardEntity.setName(request.getName());
                List<TypeYard> typeList = typeYardRepository.findAll();
                HashMap<String, Integer> typeMapper = new HashMap<>();
                typeList.forEach(typeYard -> {
                    typeMapper.put(typeYard.getTypeName().toUpperCase(), typeYard.getId());
                });
                subYardEntity.setTypeYard(typeMapper.get(request.getType() == 3 ? "3 VS 3" : "5 VS 5"));
                subYardRepository.save(subYardEntity);

                updateSlots(ownerId, request.getSlots(), request.getId());
            }
        }
    }

    private void updateSlots(String ownerId, List<SlotRequest> slots, String subYardId) {
        List<SlotEntity> slotEntities = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);

        List<SlotInfo> slotRequests = slots.stream().map(slot -> {
            return SlotInfo.getSlotInfo(slot);
        }).collect(Collectors.toList());

        List<SlotInfo> slotEntitiesInfo = new ArrayList<>();
        HashMap<SlotInfo, SlotEntity> slotInfoToSlotEntityMapper = new HashMap<>();
        for (SlotEntity slotEntity : slotEntities) {
            SlotInfo slotInfo = SlotInfo.getSlotInfo(slotEntity);
            slotEntitiesInfo.add(slotInfo);
            slotInfoToSlotEntityMapper.put(slotInfo, slotEntity);
        }

        //Voi moi slot entity (trong db), tim tat ca cac slot request xem có cai nao giong khong,
        //neu giong het, ko update
        //neu khac gia, update gia
        //neu khac het (th còn lại), thêm slot request vào db, inactive slot entity
        for (SlotInfo slotEntityInfo : slotEntitiesInfo) {
            boolean isContainInRequests = false;
            for (SlotInfo slotRequest : slotRequests) {
                if (slotEntityInfo.equals(slotRequest)) {
                    slotRequest.setExistedInStorage(true);
                    isContainInRequests = true;
                    break;
                } else if (slotEntityInfo.isPriceChange(slotRequest)) {
                    SlotEntity slotEntity = slotInfoToSlotEntityMapper.get(slotEntityInfo);
                    slotEntity.setPrice(slotRequest.getPrice());
                    slotRepository.save(slotEntity);
                    slotRequest.setExistedInStorage(true);
                    isContainInRequests = true;
                    break;
                }
            }
            if (!isContainInRequests) {
                SlotEntity slotEntity = slotInfoToSlotEntityMapper.get(slotEntityInfo);
                inactivationService.processInactivateSlot(ownerId, slotEntity.getId(), "The slot is inactivate by owner after update yard information");
            }
        }
        for (SlotInfo slotInfo : slotRequests) {
            if (!slotInfo.isExistedInStorage()) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                SlotEntity slotEntity = SlotEntity.builder().startTime(LocalTime.parse(slotInfo.getStart(), dateTimeFormatter))
                        .endTime(LocalTime.parse(slotInfo.getEnd(), dateTimeFormatter))
                        .price(slotInfo.getPrice())
                        .refYard(subYardId)
                        .active(true)
                        .build();
                slotRepository.save(slotEntity);
            }
        }
    }

    private int getSlotDuration(String slotDurationRequest) {
        String[] getHourAndMinute = slotDurationRequest.split(":");
        int hour = Integer.parseInt(getHourAndMinute[0]);
        int minute = Integer.parseInt(getHourAndMinute[1]);
        return hour * 60 + minute;
    }
}
