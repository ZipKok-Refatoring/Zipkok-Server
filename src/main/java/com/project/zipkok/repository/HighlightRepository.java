package com.project.zipkok.repository;

import com.project.zipkok.model.Highlight;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {

    @Query("SELECT h FROM Highlight h WHERE h.user.userId = :userId")
    List<Highlight> findAllByUserId(Long userId);

    Highlight findByUserAndTitle(User user, String title);
}
