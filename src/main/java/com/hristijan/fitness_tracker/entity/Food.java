package com.hristijan.fitness_tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
public class Food {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID foodId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Food name is required")
    private String name;

    @NotNull 
    @PositiveOrZero(message = "Calories must be a positive number or zero")
    private Integer calories;
    
    @NotNull 
    @PositiveOrZero(message = "Protein must be a positive number or zero")
    private Integer protein;

    @NotNull
    @PositiveOrZero(message = "Carbs must be a positive number or zero")
    private Integer carbs;    
    
    @NotNull 
    @PositiveOrZero(message = "Fats must be a positive number or zero")
    private Integer fats;
    
    @NotNull(message = "isSaved must be specified")
    private Boolean isSaved;
}
