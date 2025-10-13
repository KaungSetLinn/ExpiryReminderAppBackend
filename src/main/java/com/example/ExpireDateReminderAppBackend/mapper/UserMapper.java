package com.example.ExpireDateReminderAppBackend.mapper;

import com.example.ExpireDateReminderAppBackend.dto.UserDto;
import com.example.ExpireDateReminderAppBackend.dto.UserResponseDto;
import com.example.ExpireDateReminderAppBackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // DTO → Entity (include password when saving/updating)
    User toEntity(UserDto userDto);

    // Entity → Response DTO (exclude password)
    UserResponseDto toResponseDto(User user);
}
