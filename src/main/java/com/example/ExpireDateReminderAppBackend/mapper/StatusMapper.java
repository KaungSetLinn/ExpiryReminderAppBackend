package com.example.ExpireDateReminderAppBackend.mapper;

import com.example.ExpireDateReminderAppBackend.dto.StatusDto;
import com.example.ExpireDateReminderAppBackend.entity.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {
    StatusDto toDto(Status status);
    Status toEntity(StatusDto statusDto);
}
