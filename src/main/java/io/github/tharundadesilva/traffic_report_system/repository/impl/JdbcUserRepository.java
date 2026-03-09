package io.github.tharundadesilva.traffic_report_system.repository.impl;

import io.github.tharundadesilva.traffic_report_system.model.User;
import io.github.tharundadesilva.traffic_report_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String USERNAME = "username";

    private final RowMapper<User> userRowMapper = (rs,rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString(USERNAME))
            .password(rs.getString("password"))
            .email(rs.getString("email"))
            .mobile(rs.getString("mobile"))
            .role(rs.getString("role"))
            .isActive(rs.getBoolean("is_active"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, email, mobile, role, is_active)"+
                     "VALUES (:username, :password, :email, :mobile, :role, :isActive)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USERNAME,user.getUsername())
                .addValue("password", user.getPassword()) // Hashed password from Service
                .addValue("email", user.getEmail())
                .addValue("mobile", user.getMobile())
                .addValue("role", "USER")
                .addValue("isActive", 1);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql,params,keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        user.setRole("USER");
        return user;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = :username";
        return jdbcTemplate.query(sql,new MapSqlParameterSource(USERNAME,username),userRowMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), userRowMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
