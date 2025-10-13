package com.example.ExpireDateReminderAppBackend.mapper;

import com.example.ExpireDateReminderAppBackend.dto.CategoryDto;
import com.example.ExpireDateReminderAppBackend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Entity → DTO
    CategoryDto toDto(Category category);

    // DTO → Entity
    Category toEntity(CategoryDto categoryDto);
}
