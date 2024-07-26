package com.project.zipkok.repository;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import com.project.zipkok.model.Zim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ZimRepository extends JpaRepository<Zim, Long> {
    Boolean existsByUserAndRealEstate(User user, RealEstate realEstate);

    Zim findByUser(User user);

    List<Zim> findAllByUser(User user);

    @Query("SELECT CASE WHEN COUNT(z) > 0 THEN TRUE ELSE FALSE END FROM Zim z JOIN z.realEstate re WHERE z.user.userId = :userId AND re.realEstateId = :realEstateId")
    Boolean existsByUserIdAndRealEstateId(Long userId, Long realEstateId);
}
