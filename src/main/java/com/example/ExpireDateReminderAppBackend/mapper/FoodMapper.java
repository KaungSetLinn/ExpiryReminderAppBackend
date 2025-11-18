package com.example.ExpireDateReminderAppBackend.mapper;

import com.example.ExpireDateReminderAppBackend.dto.FoodDto;
import com.example.ExpireDateReminderAppBackend.dto.TopFoodDto;
import com.example.ExpireDateReminderAppBackend.entity.Food;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "status.statusId", target = "statusId")
    @Mapping(source = "status.statusName", target = "statusName")
    @Mapping(source = "currentContents", target = "currentContents")
    FoodDto toDto(Food food);

    @InheritInverseConfiguration
    Food toEntity(FoodDto foodDto);

    @Mapping(source = "key", target = "foodName")
    @Mapping(source = "value", target = "count")
    TopFoodDto toTopFoodDto(Map.Entry<String, Long> entry);
}
