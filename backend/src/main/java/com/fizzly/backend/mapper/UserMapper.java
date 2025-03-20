package com.fizzly.backend.mapper;

import com.fizzly.backend.dto.UserCreateDTO;
import com.fizzly.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreateDTO createDTO);
}
