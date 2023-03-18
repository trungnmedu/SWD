package com.swd.backend.repository;

import com.swd.backend.entity.TypeYard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeYardRepository extends JpaRepository<TypeYard, Integer> {
    public TypeYard getTypeYardById(int id);
}
