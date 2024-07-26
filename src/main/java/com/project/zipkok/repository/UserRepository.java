package com.project.zipkok.repository;

import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String userEmail);

    @EntityGraph(attributePaths = {"zims", "koks"})
    User findByUserId(long userId);


}
