package com.project.zipkok.repository;

import com.project.zipkok.model.Option;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllByUser(User user);
    Option findByOptionId(long optionId);
}
