package tech.devluan.task_app.user.model.dto.login;

public record LoginRequest(
    String email,
    String password
) {

}
