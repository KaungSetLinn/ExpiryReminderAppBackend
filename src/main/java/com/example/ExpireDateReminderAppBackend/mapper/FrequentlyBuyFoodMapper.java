package com.example.ExpireDateReminderAppBackend.mapper;


import com.example.ExpireDateReminderAppBackend.dto.FrequentlyBuyFoodDto;
import com.example.ExpireDateReminderAppBackend.entity.FrequentlyBuyFood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FrequentlyBuyFoodMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    FrequentlyBuyFoodDto toDto(FrequentlyBuyFood frequentlyBuyFood);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "memo", source = "memo")
    FrequentlyBuyFood toEntity(FrequentlyBuyFoodDto frequentlyBuyFoodDto);
}
