package tech.devluan.task_app.user.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
    import tech.devluan.task_app.user.model.User;
import tech.devluan.task_app.user.model.dto.register.CreationUserDTO;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(target = "id", ignore = true)
    User toEntity(CreationUserDTO userDTO);

    CreationUserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget User user, CreationUserDTO userDTO);
}
