package com.project.zipkok.repository;

import com.project.zipkok.model.DetailOption;
import com.project.zipkok.model.Highlight;
import com.project.zipkok.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailOptionRepository extends JpaRepository<DetailOption, Long> {
    List<DetailOption> findAllByOption(Option option);
    DetailOption findByDetailOptionId(long detailOptionId);
}
