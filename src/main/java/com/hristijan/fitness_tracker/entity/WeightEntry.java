package com.hristijan.fitness_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weight_entries")
@Getter
@Setter
@NoArgsConstructor
public class WeightEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID weightEntryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull 
    @PositiveOrZero(message = "Weight must be a positive number or zero")
    private Double weight;

    @NotNull 
    private LocalDate time;
}
