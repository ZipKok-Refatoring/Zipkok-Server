package com.project.zipkok.repository;

import com.project.zipkok.dto.GetKokWithZimStatus;
import com.project.zipkok.model.Kok;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface KokRepository extends JpaRepository<Kok, Long> {

    Optional<Kok> findByKokId(long kokId);

    @Query(value = "select new com.project.zipkok.dto.GetKokWithZimStatus(k, " +
            "CASE " +
            "WHEN z.user IS NOT NULL THEN TRUE ELSE FALSE " +
            "END ) " +
            "FROM Kok k " +
            "LEFT JOIN Zim z ON k.user.userId = z.user.userId AND k.realEstate.realEstateId = z.realEstate.realEstateId " +
            "WHERE k.user.userId = :userId ")
    Slice<GetKokWithZimStatus> getKokWithZimStatus(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT k "
            + "FROM Kok k "
            + "JOIN FETCH k.checkedOptions co "
            + "JOIN FETCH k.checkedDetailOptions cdo "
            + "JOIN FETCH co.option o "
            + "JOIN FETCH cdo.detailOption do "
            + "WHERE k.kokId = :kokId"
    )
    Optional<Kok> findKokWithCheckedOptionAndCheckedDetailOption(Long kokId);

    @Query("SELECT k "
            + "FROM Kok k "
            + "JOIN FETCH k.checkedImpressions ci "
            + "JOIN FETCH k.star s "
            + "JOIN FETCH ci.impression i "
            + "WHERE k.kokId = :kokId"
    )
    Optional<Kok> findKokWithImpressionAndStar(Long kokId);
}
