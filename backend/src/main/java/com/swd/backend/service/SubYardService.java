package com.swd.backend.service;

import com.swd.backend.api.v1.sub_yard.get_by_owner.GetSubYardDetailResponse;
import com.swd.backend.entity.SlotEntity;
import com.swd.backend.entity.SubYardEntity;
import com.swd.backend.entity.YardEntity;
import com.swd.backend.model.Slot;
import com.swd.backend.model.SubYardModel;
import com.swd.backend.model.model_builder.ListSlotBuilder;
import com.swd.backend.myrepository.SubYardCustomRepository;
import com.swd.backend.repository.SlotRepository;
import com.swd.backend.repository.SubYardRepository;
import com.swd.backend.repository.TypeYardRepository;
import com.swd.backend.repository.YardRepository;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubYardService {
    private SubYardCustomRepository subYardCustomRepository;
    private SubYardRepository subYardRepository;
    private TypeYardRepository typeYardRepository;
    private SlotRepository slotRepository;
    private YardRepository yardRepository;

    private List<SubYardModel> getListSubYardModelFromQueriedList(List<?> queriedSubYards) {
        return queriedSubYards.stream().map(object -> {
            if (object instanceof SubYardEntity) {
                SubYardEntity subYardEntity = (SubYardEntity) object;
                String typeYard = typeYardRepository.getTypeYardById(subYardEntity.getTypeYard()).getTypeName();
                return SubYardModel.builder()
                        .id(subYardEntity.getId())
                        .name(subYardEntity.getName())
                        .typeYard(typeYard)
                        .parentYard(subYardEntity.getParentYard())
                        .createAt(subYardEntity.getCreateAt())
                        .isActive(subYardEntity.isActive())
                        .reference(subYardEntity.getReference())
                        .build();
            } else {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<GetSubYardDetailResponse> getAllSubYardDetailOfYard(String yardId) {
        List<GetSubYardDetailResponse> subYardDetailResponses = new ArrayList<>();
        List<SubYardEntity> subYardEntities = findAllSubYardByParentId(yardId);
        for (SubYardEntity subYardEntity : subYardEntities) {
            subYardDetailResponses.add(processGetSubYardDetailResponseByOwner(subYardEntity.getId()));
        }
        return subYardDetailResponses;
    }

    public List<SubYardModel> getSubYardsByBigYard(String bigYardId) {
        List<?> queriedSubYards = findAllSubYardByParentId(bigYardId);

        return getListSubYardModelFromQueriedList(queriedSubYards);
    }

    public List<SubYardModel> getActiveSubYardsByBigYard(String bigYardId) {
        List<?> queriedSubYards = findActiveSubYardByParentId(bigYardId);

        return getListSubYardModelFromQueriedList(queriedSubYards);
    }

    private List<SubYardEntity> findAllSubYardByParentId(String bigYardId) {
        return subYardCustomRepository.getAllSubYardByBigYard(bigYardId);
    }

    private List<SubYardEntity> findActiveSubYardByParentId(String bigYardId) {
        return subYardCustomRepository.getAllActiveSubYardByBigYard(bigYardId);
    }

    public GetSubYardDetailResponse getSubYardDetailResponse(String ownerId, String yardId, String subYardId) {
        YardEntity yardEntity = yardRepository.findYardEntityByIdAndDeleted(yardId, false);

        if (yardEntity == null) {
            throw new RuntimeException("The yard is deleted or not existed");
        }

        if (!yardEntity.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("The owner is not author of this yard.");
        }

        if (!getBigYardIdFromSubYard(subYardId).equals(yardId)) {
            throw new RuntimeException("The sub-yard is not in this yard");
        }

        return processGetSubYardDetailResponseByOwner(subYardId);
    }

    public GetSubYardDetailResponse processGetSubYardDetailResponseByOwner(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        String type = typeYardRepository.getTypeYardById(subYardEntity.getTypeYard()).getTypeName();
        List<SlotEntity> slotEntities = slotRepository.findSlotEntitiesByRefYardAndActiveIsTrue(subYardId);
        List<Slot> slots = ListSlotBuilder.getAvailableSlotsFromSlotEntities(slotEntities);


        return GetSubYardDetailResponse.builder()
                .type(type)
                .name(subYardEntity.getName())
                .isActive(subYardEntity.isActive())
                .slots(slots)
                .build();
    }

    public boolean isActiveSubYard(String subYardId) {
        SubYardEntity subYard = subYardRepository.getSubYardEntityByIdAndActiveAndDeletedIsFalse(subYardId, true);
        return subYard != null;
    }

    public String getBigYardIdFromSubYard(String subYardId) {
        return subYardCustomRepository.getBigYardIdFromSubYard(subYardId);
    }

    @Transactional
    public void setInactivationInfoToSubYardEntity(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        subYardEntity.setActive(false);
        subYardEntity.setUpdatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        subYardRepository.save(subYardEntity);
    }

    @Transactional
    public void setIsParentActiveFalseForSubYard(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        subYardEntity.setParentActive(false);
        subYardRepository.save(subYardEntity);
    }

    @Transactional
    public void setActivationInfoToSubYardEntity(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        subYardEntity.setActive(true);
        subYardEntity.setUpdatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        subYardRepository.save(subYardEntity);
    }

    @Transactional
    public void setIsParentActiveTrueForSubYard(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        subYardEntity.setParentActive(true);
        subYardRepository.save(subYardEntity);
    }

    @Transactional
    public void setDeletedInfoToSubYardEntity(String subYardId) {
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntitiesById(subYardId);
        subYardEntity.setDeleted(true);
        subYardEntity.setUpdatedAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE));
        subYardRepository.save(subYardEntity);
    }

    public SubYardEntity getSubYardById(String subYardId) {
        return subYardRepository.getSubYardEntitiesById(subYardId);
    }
}
