package com.project.zipkok.repository;

import com.project.zipkok.dto.PostUpdateKokOptionRequest;
import com.project.zipkok.model.Highlight;
import com.project.zipkok.model.Option;
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
public class OptionBulkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Option> options) {
        String sql = "INSERT INTO options (user_id, is_visible, order_num, name, category) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Option option = options.get(i);

                ps.setLong(1, option.getUser().getUserId());
                ps.setBoolean(2, option.isVisible());
                ps.setLong(3, option.getOrderNum());
                ps.setString(4, option.getName());
                ps.setString(5, option.getCategory().toString());
            }

            @Override
            public int getBatchSize() {
                return options.size();
            }
        });
    }

    @Transactional
    public void updateAll(List<PostUpdateKokOptionRequest.Option> requestOptions) {
        String sql = "UPDATE Options SET is_visible = ?, order_num = ? WHERE option_id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostUpdateKokOptionRequest.Option requestOption = requestOptions.get(i);

                ps.setLong(1, requestOption.getOptionId());
                ps.setBoolean(2, requestOption.isVisible());
                ps.setLong(3, requestOption.getOrderNumber());
            }

            @Override
            public int getBatchSize() {
                return requestOptions.size();
            }
        });
    }
}
