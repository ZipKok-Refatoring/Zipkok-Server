package com.project.zipkok.repository;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KokRepository extends JpaRepository<Kok, Long> {
    boolean existsByUserAndRealEstate(User user, RealEstate realEstate);

    Kok findByKokId(long kokId);
}
