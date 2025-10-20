package com.example.ExpireDateReminderAppBackend.mapper;

import com.example.ExpireDateReminderAppBackend.dto.FoodDto;
import com.example.ExpireDateReminderAppBackend.entity.Food;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "status.statusId", target = "statusId")
    @Mapping(source = "status.statusName", target = "statusName")
    FoodDto toDto(Food food);

    @InheritInverseConfiguration
    Food toEntity(FoodDto foodDto);
}
