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
public class WeightEntryResponseDTO {
    private UUID weightEntryId;
    private UUID userId;
    private Double weight;
    private LocalDate time;
}
