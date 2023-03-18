package com.swd.backend.repository;

import com.swd.backend.entity.YardPictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YardPictureRepository extends JpaRepository<YardPictureEntity, Integer> {
    List<YardPictureEntity> getAllByRefIdOrderById(String refId);

    YardPictureEntity findTop1ByRefIdAndImage(String refId, String image);
}
