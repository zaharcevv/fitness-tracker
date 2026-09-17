package com.hristijan.fitness_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDTO {
    
    @NotBlank(message = "Username is required")
    String username;

    private Integer dailyCalorieTarget;

    private Integer dailyProteinTarget;

    private Integer dailyCarbsTarget;

    private Integer dailyFatsTarget;
}
