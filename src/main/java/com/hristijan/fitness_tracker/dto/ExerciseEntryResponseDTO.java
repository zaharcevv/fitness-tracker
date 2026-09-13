package com.hristijan.fitness_tracker.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDate;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class ExerciseEntryResponseDTO {
    private UUID exerciseEntryId;
    private UUID exerciseId;
    private UUID userId;
    private String name;
    private String muscleGroup;
    private Boolean isSaved;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private LocalDate time;
}
