package com.project.zipkok.repository;

import com.project.zipkok.model.Impression;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ImpressionRepositoryCustomImpl implements ImpressionRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void insertImpressions(List<Impression> impressions) {
        if (impressions == null || impressions.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO impression (impression_title, user_id) VALUES ");

        for (int i = 0; i < impressions.size(); i++) {
            sql.append("(:impressionTitle").append(i).append(", :userId").append(i).append(")");
            if (i < impressions.size() - 1) {
                sql.append(", ");
            }
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        for (int i = 0; i < impressions.size(); i++) {
            query.setParameter("impressionTitle" + i, impressions.get(i).getImpressionTitle());
            query.setParameter("userId" + i, impressions.get(i).getUser().getUserId());
        }

        query.executeUpdate();
    }
}
