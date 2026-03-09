package io.github.tharundadesilva.traffic_report_system.dto.Auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
