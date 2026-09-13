package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FoodRepository extends JpaRepository<Food, UUID> {
    
}
