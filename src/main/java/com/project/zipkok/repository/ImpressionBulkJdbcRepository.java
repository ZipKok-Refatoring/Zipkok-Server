package com.project.zipkok.repository;

import com.project.zipkok.model.Highlight;
import com.project.zipkok.model.Impression;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ImpressionBulkJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Impression> impressions) {
        String sql = "INSERT INTO impression (user_id, impression_title) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Impression impression = impressions.get(i);

                ps.setLong(1, impression.getUser().getUserId());
                ps.setString(2, impression.getImpressionTitle());
            }

            @Override
            public int getBatchSize() {
                return impressions.size();
            }
        });
    }
}
