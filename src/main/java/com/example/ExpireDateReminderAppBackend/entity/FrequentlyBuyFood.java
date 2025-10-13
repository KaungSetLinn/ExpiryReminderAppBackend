package com.example.ExpireDateReminderAppBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 0H02009_カゥンセッリン
 */

@Entity
@Data
@Table(name = "frequently_buy_food")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrequentlyBuyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frequently_buy_food_id")
    private Long frequentlyBuyFoodId;

    @Column(name = "frequently_buy_food_name")
    private String frequentlyBuyFoodName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 50)
    private String unit; // e.g. パック

    @Column(name = "total_contents")
    private Integer totalContents;

    @Column(name = "content_unit", length = 50)
    private String contentUnit; // e.g. 個, 本, etc.

    @Column(name = "food_image_url")
    private String foodImageUrl;

    private String memo;

    @Column(name = "reminder_days_before_expire")
    private Integer reminderDaysBeforeExpire;

}
