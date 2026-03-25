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

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public User register(User user){
        user.setPassword(encoder.encode(user.getPassword()));
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            user.setRoles(Set.of("USER"));
//        }

        //remove this after introducing the email verification
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto request){


        User user = userRepository.findByUsername(request.getUsername());

        if(Boolean.TRUE.equals(verify(request))){
            String token = jwtService.generateToken(user.getId(), request.getUsername(), user.getRoles() );
            AuthResponseDto response = new AuthResponseDto();
            response.setRoles(user.getRoles());
            response.setUsername(user.getUsername());
            response.setId(user.getId());
            response.setDefaultPortal(user.getDefaultPortal());
            response.setToken(token);
            return response;
        }else{
            throw new BadCredentialsException("invalid credentials");
        }


    }

    public String update(User user){
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(user.getPassword()));
        } else {
            User existingUser = userRepository.findById(user.getId());
            user.setPassword(existingUser.getPassword());
        }
        userRepository.update(user);
        return "User Updated Successfully";
    }

    public Boolean verify(LoginRequestDto request) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        return authentication.isAuthenticated();
    }

    public User findUserById(Long userId)  {
        User user = userRepository.findById(userId);
        if(user==null) throw new RuntimeException("User not found");
        return user;
    }

    public String deleteUser(Long userId){
        Long rowsAffected = userRepository.delete(userId);
        if(rowsAffected!=0){
            return "User deleted successfully";
        }else{
            return "User deletion unsuccessful";
        }

    }


    public void logout() {
        // The AuditAspect intercepts this and handles the database entry.
    }
}
