package by.fizzly.backend.mapper;

import by.fizzly.backend.entity.User;
import by.fizzly.common.dto.UserCreateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreateDTO createDTO);
}
