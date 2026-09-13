package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FoodEntryRepository extends JpaRepository<FoodEntry, UUID> {
    @Query("SELECT fe FROM FoodEntry fe WHERE fe.user.userId = :userId AND fe.time = :date")
    List<FoodEntry> findByUserAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Query("SELECT fe FROM FoodEntry fe WHERE fe.user.userId = :userId AND fe.time BETWEEN :startDate AND :endDate")
    List<FoodEntry> findByUserAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
