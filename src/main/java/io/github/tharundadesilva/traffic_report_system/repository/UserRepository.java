package io.github.tharundadesilva.traffic_report_system.repository;

import io.github.tharundadesilva.traffic_report_system.model.User;

public interface UserRepository {

    User save(User user);

    User findByUsername(String username);

    User findById(Long id);
}
