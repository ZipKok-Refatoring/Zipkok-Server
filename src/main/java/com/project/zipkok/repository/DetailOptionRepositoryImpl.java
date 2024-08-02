package com.project.zipkok.repository;

import com.project.zipkok.model.DetailOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class DetailOptionRepositoryImpl implements DetailOptionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void insertDetailOptions(List<DetailOption> detailOptions) {
        if (detailOptions == null || detailOptions.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO detailoption (name, is_visible, option_id) VALUES ");

        for (int i = 0; i < detailOptions.size(); i++) {
            sql.append("(:name").append(i).append(", :isVisible").append(i)
                    .append(", :optionId").append(i).append(")");
            if (i < detailOptions.size() - 1) {
                sql.append(", ");
            }
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        for (int i = 0; i < detailOptions.size(); i++) {
            query.setParameter("name" + i, detailOptions.get(i).getName());
            query.setParameter("isVisible" + i, detailOptions.get(i).isVisible());
            query.setParameter("optionId" + i, detailOptions.get(i).getOption().getOptionId());
        }

        query.executeUpdate();
    }
}
