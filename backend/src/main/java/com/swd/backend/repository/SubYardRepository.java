package com.swd.backend.repository;

import com.swd.backend.entity.SubYardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SubYardRepository extends JpaRepository<SubYardEntity, String> {
    public SubYardEntity getSubYardEntityByIdAndActiveAndDeletedIsFalse(String id, boolean isActive);

    public SubYardEntity getSubYardEntitiesById(String id);

    @Query("SELECT subYard.id FROM SubYardEntity subYard WHERE subYard.parentYard IN :listSubYardId")
    public List<String> getAllSubYardIdByListBigYardId(@Param("listSubYardId") Collection<String> listSubYardId);

    public List<SubYardEntity> findSubYardEntitiesByParentYardAndParentActiveAndDeleted(String yardId, boolean isParentActive, boolean isDeleted);
}
