package tech.devluan.task_app.user.model.dto.register;

import java.util.UUID;

public record ResponseUserDTO(
    UUID id,
    String name,
    String email
) {

}
