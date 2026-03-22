package io.github.tharundadesilva.traffic_report_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private Set<String> roles; // Stores 'ADMIN' or 'USER'
    private boolean isActive;
    private LocalDateTime createdAt;
}
