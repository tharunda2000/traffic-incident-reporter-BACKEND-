package io.github.tharundadesilva.traffic_report_system.controller;

import io.github.tharundadesilva.traffic_report_system.dto.Auth.AuthResponseDto;
import io.github.tharundadesilva.traffic_report_system.dto.Auth.LoginRequestDto;
import io.github.tharundadesilva.traffic_report_system.model.ApiResponse;
    import io.github.tharundadesilva.traffic_report_system.model.User;
    import io.github.tharundadesilva.traffic_report_system.service.AuthService;
    import jakarta.servlet.http.HttpServletRequest;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/v1/auth")
    @RequiredArgsConstructor
    public class AuthController {

        private final AuthService authService;

        @CrossOrigin(origins = "http://localhost:5173")
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginRequestDto request, HttpServletRequest http){
            return ResponseEntity.ok(ApiResponse.ok(authService.login(request), http.getRequestURI()));
        }

        @PostMapping("/register")
        public ResponseEntity<ApiResponse<User>> register(@RequestBody User user,HttpServletRequest http){
            return ResponseEntity.ok(ApiResponse.ok(authService.register(user), http.getRequestURI()));
        }

        @PutMapping("/update")
        public ResponseEntity<ApiResponse<String>> update(@RequestBody User user,HttpServletRequest http){
            return ResponseEntity.ok(ApiResponse.ok(authService.update(user), http.getRequestURI()));
        }

        @GetMapping("/findById")
        public ResponseEntity<ApiResponse<User>> findById(@RequestParam Long userId,HttpServletRequest http){
            return ResponseEntity.ok(ApiResponse.ok(authService.findUserById(userId), http.getRequestURI()));
        }

        @DeleteMapping("/delete")
        public ResponseEntity<ApiResponse<String>> delete(@RequestParam Long userId,HttpServletRequest http){
            return ResponseEntity.ok(ApiResponse.ok(authService.deleteUser(userId), http.getRequestURI()));
        }
        @CrossOrigin(origins = "http://localhost:5173")
        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest http) {
            authService.logout();
            return ResponseEntity.ok(ApiResponse.ok(null, "Logged out", http.getRequestURI()));
        }

    }
