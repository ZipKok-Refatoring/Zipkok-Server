package com.project.zipkok.repository;

import com.project.zipkok.dto.GetRealEstateResponse;
import com.project.zipkok.model.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    RealEstate findById(long realEstateId);

    @Query(nativeQuery = true,
            value = "SELECT *, (ABS(r.latitude - :latitude) + ABS(r.longitude - :longitude)) AS distance " +
                    "FROM RealEstate r " +
                    "ORDER BY distance " +
                    "LIMIT 5")
    List<RealEstate> findAllByProximity(@Param(value = "latitude") double latitude, @Param(value = "longitude") double longitude);

    List<RealEstate> findByLatitudeBetweenAndLongitudeBetween(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude);
}
