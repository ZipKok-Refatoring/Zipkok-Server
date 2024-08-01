package com.project.zipkok.repository;

import com.project.zipkok.model.Impression;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpressionRepository extends JpaRepository<Impression, Long>, ImpressionRepositoryCustom {
    Impression findByUserAndImpressionTitle(User user, String title);
}
