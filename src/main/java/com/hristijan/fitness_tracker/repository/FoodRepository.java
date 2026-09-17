package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface FoodRepository extends JpaRepository<Food, UUID> {

    @Query("SELECT f FROM Food f WHERE f.user.userId = :userId")
    List<Food> findByUser(@Param("userId") UUID userId);
}
