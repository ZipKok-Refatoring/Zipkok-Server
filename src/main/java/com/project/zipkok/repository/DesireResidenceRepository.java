package com.project.zipkok.repository;

import com.project.zipkok.model.DesireResidence;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesireResidenceRepository extends JpaRepository<DesireResidence, Long> {
    DesireResidence findByUser(User user);
}