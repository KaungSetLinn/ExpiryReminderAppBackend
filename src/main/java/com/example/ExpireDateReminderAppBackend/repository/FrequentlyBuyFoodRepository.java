package com.example.ExpireDateReminderAppBackend.repository;

import com.example.ExpireDateReminderAppBackend.entity.FrequentlyBuyFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrequentlyBuyFoodRepository extends JpaRepository<FrequentlyBuyFood, Long> {
    List<FrequentlyBuyFood> findByUser_Id(Long userId);

    List<FrequentlyBuyFood> findByCategory_Id(Long categoryId);

    // ✅ New method: find by userId and order by frequentlyBuyFoodId descending
    List<FrequentlyBuyFood> findByUser_IdOrderByFrequentlyBuyFoodIdDesc(Long userId);
}
