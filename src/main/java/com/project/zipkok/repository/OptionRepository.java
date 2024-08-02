package com.project.zipkok.repository;

import com.project.zipkok.model.Option;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long>, OptionRepositoryCustom {

    Option findByOptionId(long optionId);

    @Query("select o from Option o JOIN FETCH o.detailOptions where o.user.userId = :userId")
    List<Option> findAllByUserId(long userId);
}
