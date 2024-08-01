package com.project.zipkok.repository;

import com.project.zipkok.model.Highlight;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HighlightRepositoryCustomImpl implements HighlightRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void insertHighlights(List<Highlight> highlights) {
        if (highlights == null || highlights.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO highlight (title, user_id) VALUES ");

        for (int i = 0; i < highlights.size(); i++) {
            sql.append("(:title").append(i).append(", :userId").append(i).append(")");
            if (i < highlights.size() - 1) {
                sql.append(", ");
            }
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        for (int i = 0; i < highlights.size(); i++) {
            query.setParameter("title" + i, highlights.get(i).getTitle());
            query.setParameter("userId" + i, highlights.get(i).getUser().getUserId());
        }

        query.executeUpdate();
    }

}
