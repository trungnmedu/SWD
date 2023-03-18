package com.swd.backend.repository;

import com.swd.backend.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    ProvinceEntity findDistinctById(int id);
}
