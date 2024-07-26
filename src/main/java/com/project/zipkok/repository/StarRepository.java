package com.project.zipkok.repository;

import com.project.zipkok.model.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
}
