package tech.devluan.task_app.user.model.dto.login;

public record LoginResponse(
    String token,
     String expiresIn
) {
}
