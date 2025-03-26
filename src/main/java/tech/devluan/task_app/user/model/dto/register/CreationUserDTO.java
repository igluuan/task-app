package tech.devluan.task_app.user.model.dto.register;

public record CreationUserDTO(
    String name,
    String email,
    String password
) {

}
