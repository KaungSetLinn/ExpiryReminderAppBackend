package com.example.ExpireDateReminderAppBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 0H02009_カゥンセッリン
 */
@Entity
@Data
@Table(name = "food")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    private Integer quantity; // e.g. 1
    private String unit; // e.g. パック

    @Column(name = "total_contents")
    private Integer totalContents;

    @Column(name = "current_contents")
    private Integer currentContents;

    @Column(name = "content_unit")
    private String contentUnit; // e.g. 個

    @Column(name = "expire_date")
    private LocalDate expireDate;

    private String memo;

    @Column(name = "food_image_url")
    private String foodImageUrl;

}
