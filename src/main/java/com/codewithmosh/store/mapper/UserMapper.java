package com.codewithmosh.store.mapper;

import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.entities.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User ToEntity(RegisterUserDto request);
}
