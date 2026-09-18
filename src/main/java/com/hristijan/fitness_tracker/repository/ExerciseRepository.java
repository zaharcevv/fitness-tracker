package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    @Query("SELECT e FROM Exercise e WHERE e.user.userId = :userId")
    List<Exercise> findByUser(@Param("userId") UUID userId);
}
