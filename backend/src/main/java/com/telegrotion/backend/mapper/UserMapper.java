package com.telegrotion.backend.mapper;

import com.telegrotion.backend.dto.UserCreateDTO;
import com.telegrotion.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreateDTO createDTO);
}
