package com.project.zipkok.repository;

import com.project.zipkok.model.TransactionPriceConfig;
import com.project.zipkok.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionPriceConfigRepository extends JpaRepository<TransactionPriceConfig, Long> {
    TransactionPriceConfig findByUser(User user);
}
