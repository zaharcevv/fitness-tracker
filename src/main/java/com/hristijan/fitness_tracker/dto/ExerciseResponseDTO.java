package com.hristijan.fitness_tracker.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
public class ExerciseResponseDTO {
    private UUID exerciseId;
    private UUID userId;
    private String name;
    private String muscleGroup;
    private Boolean isSaved;
}
