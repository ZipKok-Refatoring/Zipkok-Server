package com.project.zipkok.repository;

import com.project.zipkok.model.Impression;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImpressionRepository extends JpaRepository<Impression, Long> {
    Impression findByUserAndImpressionTitle(User user, String title);
}
