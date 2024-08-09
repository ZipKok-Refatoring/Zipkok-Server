package com.project.zipkok.repository;

import com.project.zipkok.model.Option;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    @Query("SELECT o " +
            "FROM Option o " +
            "JOIN FETCH o.detailOptions do " +
            "WHERE o.user.userId = :userId")
    List<Option> findAllByUserIdWithDetailOption(Long userId);

    Option findByOptionId(long optionId);
}
