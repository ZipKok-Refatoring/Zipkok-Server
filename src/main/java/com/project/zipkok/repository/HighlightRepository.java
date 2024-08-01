package com.project.zipkok.repository;

import com.project.zipkok.model.Highlight;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long>, HighlightRepositoryCustom {
    List<Highlight> findAllByUser(User user);
}
