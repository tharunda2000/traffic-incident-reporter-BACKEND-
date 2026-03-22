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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String MOBILE = "mobile";
    private static final String USERID = "userId";

    private final RowMapper<User> userRowMapper = (rs,rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString(USERNAME))
            .password(rs.getString("password"))
            .email(rs.getString(EMAIL))
            .mobile(rs.getString(MOBILE))
            .isActive(rs.getBoolean("is_active"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, email, mobile, is_active)"+
                     "VALUES (:username, :password, :email, :mobile, :isActive)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USERNAME,user.getUsername())
                .addValue("password", user.getPassword()) // Hashed password from Service
                .addValue(EMAIL, user.getEmail())
                .addValue(MOBILE, user.getMobile())
                .addValue("isActive", 1);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql,params,keyHolder);
        long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);

        // Dynamically Insert Roles
        if (user.getRoles() != null && !user.getRoles().isEmpty()){
            String roleSql = "INSERT INTO user_roles (user_id,role_id)"+
                             "SELECT :userId, id FROM roles WHERE name = :roleName";

            // batchUpdate for efficiency if there are multiple roles
            List<MapSqlParameterSource> batchParams = user.getRoles().stream()
                    .map(roleName -> new MapSqlParameterSource()
                            .addValue(USERID, userId)
                            .addValue("roleName", roleName))
                    .toList();

            jdbcTemplate.batchUpdate(roleSql, batchParams.toArray(new MapSqlParameterSource[0]));
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = :username";
        User user = jdbcTemplate.query(sql,new MapSqlParameterSource(USERNAME,username),userRowMapper)
                .stream()
                .findFirst()
                .orElse(null);

        return populateRoles(user);
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        User user = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), userRowMapper)
                .stream()
                .findFirst()
                .orElse(null);

        return populateRoles(user);
    }

    @Transactional
    @Override
    public void update(User user) {
        // 1. Update basic user info
        String sql = "UPDATE users SET email = :email, mobile = :mobile, is_active = :isActive " +
                "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(EMAIL, user.getEmail())
                .addValue(MOBILE, user.getMobile())
                .addValue("isActive", user.isActive() ? 1 : 0)
                .addValue("id", user.getId());

        jdbcTemplate.update(sql, params);

        // 2. Sync Roles: Easiest way is to delete old ones and insert new ones
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = :id", new MapSqlParameterSource("id", user.getId()));
        insertRoles(user.getId(), user.getRoles());
    }

    @Override
    public Long delete(Long id) {
        String sql = "DELETE FROM users WHERE id = :id";

        int rowsAffected = jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));

        if (rowsAffected == 0) {
            return null;
        }

        return id;
    }

    //Helper Methods

    // 1. populate user with user roles
    private User populateRoles(User user){
        if(user == null) return null;

        String sql = "SELECT r.name FROM roles r " +
                     "JOIN user_roles ur ON r.id = ur.role_id " +
                     "WHERE ur.user_id = :userId";

        List<String> roles = jdbcTemplate.queryForList(sql,
                new MapSqlParameterSource(USERID, user.getId()), String.class);

            user.setRoles(new HashSet<>(roles));
        return user;
    }

    // 1. Insert user roles for users
    private void insertRoles(Long userId, Set<String> roles) {
        if (roles != null && !roles.isEmpty()) {
            String roleSql = "INSERT INTO user_roles (user_id, role_id) " +
                             "SELECT :userId, id FROM roles WHERE name = :roleName";

            List<MapSqlParameterSource> batchParams = roles.stream()
                    .map(roleName -> new MapSqlParameterSource()
                            .addValue(USERID, userId)
                            .addValue("roleName", roleName))
                    .toList();

            jdbcTemplate.batchUpdate(roleSql, batchParams.toArray(new MapSqlParameterSource[0]));
        }
    }
}
