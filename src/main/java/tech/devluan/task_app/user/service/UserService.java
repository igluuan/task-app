package tech.devluan.task_app.user.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import lombok.RequiredArgsConstructor;
import tech.devluan.task_app.user.model.User;
import tech.devluan.task_app.user.model.dto.login.LoginRequest;
import tech.devluan.task_app.user.model.dto.login.LoginResponse;
import tech.devluan.task_app.user.model.dto.register.CreationUserDTO;
import tech.devluan.task_app.user.repository.UserRepository;
import tech.devluan.task_app.user.service.mapper.UserMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public CreationUserDTO createUser(CreationUserDTO creationUserDTO) {
        User user = userMapper.toEntity(creationUserDTO);
        user.setPassword(passwordEncoder.encode(creationUserDTO.password()));
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Find user by email
            User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify password
            if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            // Generate JWT token
            String token = generateJwtToken(user);

            // Return login response
            return new LoginResponse(
                token,
                "1h"
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    private String generateJwtToken(User user) {
        Instant now = Instant.now();
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.HOURS))
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
