package tech.devluan.task_app.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tech.devluan.task_app.user.model.dto.login.LoginRequest;
import tech.devluan.task_app.user.model.dto.login.LoginResponse;
import tech.devluan.task_app.user.model.dto.register.CreationUserDTO;
import tech.devluan.task_app.user.service.UserService;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/newuser")
    public ResponseEntity<CreationUserDTO> registerUser(@Valid @RequestBody CreationUserDTO creationUserDTO) {
        return ResponseEntity.ok(userService.createUser(creationUserDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
