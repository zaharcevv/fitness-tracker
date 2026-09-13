package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
}
