package com.project.zipkok.repository;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface KokRepository extends JpaRepository<Kok, Long> {
    boolean existsByUserAndRealEstate(User user, RealEstate realEstate);

    Kok findByKokId(long kokId);

    @Query("SELECT k FROM Kok k JOIN FETCH k.realEstate re JOIN FETCH re.realEstateImages WHERE k.user.userId = :userId")
    Slice<Kok> findByUserId(long userId, org.springframework.data.domain.Pageable pageable);
}
