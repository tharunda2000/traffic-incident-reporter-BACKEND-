package io.github.tharundadesilva.traffic_report_system.dto.Auth;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private Long id;
    private  String username;
    private String role;
}
