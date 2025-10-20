package com.codewithmosh.store.mapper;

import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.dtos.UpdateUserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User ToEntity(RegisterUserDto request);


    void update(UpdateUserDto request, @MappingTarget User user);
}
