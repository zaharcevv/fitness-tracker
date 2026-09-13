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
public class FoodEntryResponseDTO {
    
    private UUID foodEntryId;
    private UUID foodId;
    private UUID userId;
    private String name;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fats;
    private Boolean isSaved;
    private Integer quantity;
    private LocalDate time; 
}
