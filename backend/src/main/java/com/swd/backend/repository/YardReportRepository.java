package com.swd.backend.repository;

import com.swd.backend.entity.YardReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YardReportRepository extends JpaRepository<YardReportEntity, String> {
    YardReportEntity findYardReportEntityById(String id);
}
