package com.example.ExpireDateReminderAppBackend.repository;

import com.example.ExpireDateReminderAppBackend.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByUserId(Long userId);
    List<Food> findByUserIdOrderByExpireDateAsc(Long userId);
    List<Food> findByUserIdAndStatus_StatusIdOrderByExpireDateAsc(Long userId, Long activeStatusId);
    List<Food> findByUserIdAndStatus_StatusIdNotOrderByExpireDateAsc(Long userId, Long discardedStatusId);
    List<Food> findByCategoryId(Long categoryId);
    List<Food> findByStatus_StatusId(Long statusId);
}
