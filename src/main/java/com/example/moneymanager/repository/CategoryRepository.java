package com.example.moneymanager.repository;

import com.example.moneymanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByProfileId(String profileId);

    Optional<CategoryEntity> findByIdAndProfileId(String id, String profileId);

    List<CategoryEntity> findByTypeAndProfileId(String type, String profileId);

    Boolean existsByNameAndProfileId(String name, String profileId);
}
