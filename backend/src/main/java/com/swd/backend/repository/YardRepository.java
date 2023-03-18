package com.swd.backend.repository;

import com.swd.backend.entity.YardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YardRepository extends JpaRepository<YardEntity, String> {
    public List<YardEntity> findYardEntitiesByDistrictIdAndActiveAndDeleted(int districtId, boolean isActive, boolean isDeleted);

    public YardEntity findYardEntityByIdAndActiveAndDeleted(String yardId, boolean isActive, boolean isDeleted);

    public YardEntity findYardEntitiesById(String yardId);

    @Query("SELECT yard.id FROM YardEntity yard WHERE yard.ownerId = ?1")
    public List<String> getAllYardIdByOwnerId(String ownerId);

    public List<YardEntity> findAllByOwnerIdAndDeleted(String ownerId, boolean deleted);

    public List<YardEntity> findAllByOwnerIdAndDeletedOrderByCreateAtDesc(String ownerId, boolean deleted, Pageable pageable);

    public int countAllByOwnerIdAndDeleted(String ownerId, boolean deleted);

    public YardEntity findYardEntityByIdAndDeleted(String yardId, boolean isDeleted);

    public YardEntity findYardEntityById(String yardId);

    public List<YardEntity> findYardEntitiesByOwnerIdOrderByReferenceAsc(String ownerId);

    public List<YardEntity> findYardEntitiesByOwnerIdAndDeleted(String ownerId, boolean isDeleted);
}