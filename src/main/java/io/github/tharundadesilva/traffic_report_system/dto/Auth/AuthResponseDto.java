package io.github.tharundadesilva.traffic_report_system.dto.Auth;

import lombok.Data;

import java.util.Set;

@Data
public class AuthResponseDto {
    private String token;
    private Long id;
    private  String username;
    private String defaultPortal;
    private Set<String> roles;
}
