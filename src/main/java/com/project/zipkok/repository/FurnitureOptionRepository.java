package com.project.zipkok.repository;

import com.project.zipkok.model.FurnitureOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FurnitureOptionRepository extends JpaRepository<FurnitureOption, Long> {
}
