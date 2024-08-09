package com.project.zipkok.repository;

import com.project.zipkok.dto.PostUpdateKokOptionRequest;
import com.project.zipkok.model.DetailOption;
import com.project.zipkok.model.Highlight;
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
public class DetailOptionBulkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<DetailOption> detailOptions) {
        String sql = "INSERT INTO detailoption (option_id, is_visible, name) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DetailOption detailOption = detailOptions.get(i);

                ps.setLong(1, detailOption.getOption().getOptionId());
                ps.setBoolean(2, detailOption.isVisible());
                ps.setString(3, detailOption.getName());
            }

            @Override
            public int getBatchSize() {
                return detailOptions.size();
            }
        });
    }

    @Transactional
    public void updateAll(List<PostUpdateKokOptionRequest.DetailOption> requestOptionIds) {
        String sql = "UPDATE DetailOption SET is_visible = ? WHERE detail_option_id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostUpdateKokOptionRequest.DetailOption requestDetailOption = requestOptionIds.get(i);

                ps.setLong(1, requestDetailOption.getDetailOptionId());
                ps.setBoolean(2, requestDetailOption.isDetailOptionIsVisible());
            }

            @Override
            public int getBatchSize() {
                return requestOptionIds.size();
            }
        });
    }
}
