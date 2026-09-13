package com.hristijan.fitness_tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(unique = true)
    @NotBlank (message = "Username is required")
    private String username;

    @NotBlank (message = "Password is required")
    private String password;

    private Integer dailyCalorieTarget;

    private Integer dailyProteinTarget;

    private Integer dailyCarbsTarget;

    private Integer dailyFatsTarget;
}