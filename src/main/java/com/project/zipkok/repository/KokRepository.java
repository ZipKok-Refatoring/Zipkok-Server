package com.project.zipkok.repository;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KokRepository extends JpaRepository<Kok, Long> {
    Kok findFirstByUserAndRealEstate(User user, RealEstate realEstateId);

    Kok findByKokId(long kokId);
}
