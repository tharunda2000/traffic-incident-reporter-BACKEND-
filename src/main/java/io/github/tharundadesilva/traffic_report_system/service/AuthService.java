package io.github.tharundadesilva.traffic_report_system.service;

import io.github.tharundadesilva.traffic_report_system.dto.Auth.AuthResponseDto;
import io.github.tharundadesilva.traffic_report_system.dto.Auth.LoginRequestDto;
import io.github.tharundadesilva.traffic_report_system.model.User;
import io.github.tharundadesilva.traffic_report_system.repository.UserRepository;
import io.github.tharundadesilva.traffic_report_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public User register(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto request){


        User user = userRepository.findByUsername(request.getUsername());

        if(Boolean.TRUE.equals(verify(request))){
            String token = jwtService.generateToken(user.getId(), request.getUsername(), user.getRole() );
            AuthResponseDto response = new AuthResponseDto();
            response.setToken(token);
            return response;
        }else{
            throw new BadCredentialsException("invalid credentials");
        }


    }

    public Boolean verify(LoginRequestDto request) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        return authentication.isAuthenticated();
    }
}
