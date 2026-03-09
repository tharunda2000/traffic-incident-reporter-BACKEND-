package io.github.tharundadesilva.traffic_report_system.controller;

import io.github.tharundadesilva.traffic_report_system.dto.Auth.AuthResponseDto;
import io.github.tharundadesilva.traffic_report_system.dto.Auth.LoginRequestDto;
import io.github.tharundadesilva.traffic_report_system.model.User;
import io.github.tharundadesilva.traffic_report_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto request){
        return authService.login(request);
    }

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return authService.register(user);
    }
}
