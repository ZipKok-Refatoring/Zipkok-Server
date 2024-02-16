package com.project.zipkok.repository;

import com.project.zipkok.model.Pin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {
    Pin findByPinId(Long pinId);
}
