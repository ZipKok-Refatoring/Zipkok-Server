package com.project.zipkok.repository;

import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String userEmail);

    User findByUserId(Long userId);

    @EntityGraph(attributePaths = {"zims", "koks"})
    User findByUserId(long userId);

    @Query("SELECT u "
            + "FROM User u "
            + "JOIN FETCH u.desireResidence d "
            + "JOIN FETCH u.transactionPriceConfig t "
            + "WHERE u.userId = :userId"
    )
    User findByUserIdWithDesireResidenceAndTransactionPriceConfig(Long userId);

    @Query("SELECT u "
            + "FROM User u "
            + "JOIN FETCH u.highlights h "
            + "JOIN FETCH u.options o "
            + "WHERE u.userId = :userId"
    )
    User findByUserIdWithHighlightAndOptions(Long userId);
}
