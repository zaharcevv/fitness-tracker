package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WeightEntryRepository extends JpaRepository<WeightEntry, UUID> {

    @Query("SELECT we FROM WeightEntry we WHERE we.user.userId = :userId AND we.time BETWEEN :startDate AND :endDate")
    List<WeightEntry> findByUserAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
