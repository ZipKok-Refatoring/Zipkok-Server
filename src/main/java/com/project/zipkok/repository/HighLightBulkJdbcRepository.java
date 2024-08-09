package com.project.zipkok.repository;

import com.project.zipkok.model.Highlight;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HighLightBulkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Highlight> highlights) {
        String sql = "INSERT INTO highlight (user_id, status, title) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Highlight highlight = highlights.get(i);

                ps.setLong(1, highlight.getUser().getUserId());
                ps.setString(2, highlight.getStatus());
                ps.setString(3, highlight.getTitle());
            }

            @Override
            public int getBatchSize() {
                return highlights.size();
            }
        });
    }
}
