package com.hristijan.fitness_tracker.repository;

import com.hristijan.fitness_tracker.entity.ExerciseEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExerciseEntryRepository extends JpaRepository<ExerciseEntry, UUID> {
    
}
