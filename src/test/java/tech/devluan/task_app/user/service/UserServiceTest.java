package tech.devluan.task_app.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import tech.devluan.task_app.user.model.User;
import tech.devluan.task_app.user.model.dto.login.LoginRequest;
import tech.devluan.task_app.user.model.dto.login.LoginResponse;
import tech.devluan.task_app.user.model.dto.register.CreationUserDTO;
import tech.devluan.task_app.user.model.dto.register.ResponseUserDTO;
import tech.devluan.task_app.user.repository.UserRepository;
import tech.devluan.task_app.user.service.mapper.UserMapper;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private UserService userService;

    private CreationUserDTO creationUserDTO;
    private ResponseUserDTO responseUserDTO;
    private User user;
    private LoginRequest loginRequest;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {
        creationUserDTO = new CreationUserDTO(
            "John Doe",
            "test@example.com",
            "password123"
        );

        responseUserDTO = new ResponseUserDTO(
            UUID.randomUUID(),
            "John Doe",
            "test@example.com"
        );

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName(creationUserDTO.name());
        user.setEmail(creationUserDTO.email());
        user.setPassword(creationUserDTO.password());

        loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        // Arrange
        when(userMapper.toEntity(creationUserDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseUserDTO);

        // Act
        ResponseUserDTO result = userService.createUser(creationUserDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(creationUserDTO.email());
        assertThat(result.name()).isEqualTo(creationUserDTO.name());
    }

    @Test
    void createUser_ShouldMapAndSaveUserCorrectly() {
        // Arrange
        when(userMapper.toEntity(creationUserDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseUserDTO);

        // Act
        userService.createUser(creationUserDTO);

        // Assert
        assertThat(user.getEmail()).isEqualTo(creationUserDTO.email());
        assertThat(user.getName()).isEqualTo(creationUserDTO.name());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, TEST_PASSWORD)).thenReturn(true);
        
        Jwt jwt = Jwt.withTokenValue(TEST_TOKEN)
            .header("alg", "none")
            .claim("sub", user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
            
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResponse response = userService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(TEST_TOKEN);
        assertThat(response.expiresIn()).isEqualTo("1h");
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, TEST_PASSWORD)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid password");
    }
} 