package com.project.zipkok.repository;

import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import com.project.zipkok.model.Zim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZimRepository extends JpaRepository<Zim, Long> {
    Zim findFirstByUserAndRealEstate(User user, RealEstate realEstateId);
}
