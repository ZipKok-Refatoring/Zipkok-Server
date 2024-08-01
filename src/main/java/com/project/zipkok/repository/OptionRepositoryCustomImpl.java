package com.project.zipkok.repository;

import com.project.zipkok.model.DetailOption;
import com.project.zipkok.model.Option;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class OptionRepositoryCustomImpl implements OptionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void insertOptions(List<Option> options) {
        if (options == null || options.isEmpty()) {
            return;
        }

        StringBuilder optionSql = new StringBuilder("INSERT INTO options (name, is_visible, order_num, category, user_id) VALUES ");
        int optionCounter = 0;
        for (Option option : options) {
            optionSql.append("(:name").append(optionCounter).append(", :isVisible").append(optionCounter)
                    .append(", :orderNum").append(optionCounter).append(", :category").append(optionCounter)
                    .append(", :userId").append(optionCounter).append("), ");
            optionCounter++;
        }

        optionSql.setLength(optionSql.length() - 2);
        Query optionQuery = entityManager.createNativeQuery(optionSql.toString());

        optionCounter = 0;
        for (Option option : options) {
            optionQuery.setParameter("name" + optionCounter, option.getName());
            optionQuery.setParameter("isVisible" + optionCounter, option.isVisible());
            optionQuery.setParameter("orderNum" + optionCounter, option.getOrderNum());
            optionQuery.setParameter("category" + optionCounter, option.getCategory().name());
            optionQuery.setParameter("userId" + optionCounter, option.getUser().getUserId());
            optionCounter++;
        }

        optionQuery.executeUpdate();
    }
}
