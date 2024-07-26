package com.project.zipkok.repository;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KokRepository extends JpaRepository<Kok, Long> {
    boolean existsByUserAndRealEstate(User user, RealEstate realEstate);

    Kok findByKokId(long kokId);

    @Query("SELECT k "
            + "FROM Kok k "
            + "JOIN FETCH k.checkedOptions co "
            + "JOIN FETCH k.checkedDetailOptions cdo "
            + "JOIN FETCH co.option o "
            + "JOIN FETCH cdo.detailOption do "
            + "WHERE k.kokId = :kokId"
    )
    Kok findKokWithCheckedOptionAndCheckedDetailOption(Long kokId);
}
