package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.dto.ExerciseResponseDTO;
import com.hristijan.fitness_tracker.entity.Exercise;
import com.hristijan.fitness_tracker.repository.ExerciseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hristijan.fitness_tracker.repository.UserRepository;
import com.hristijan.fitness_tracker.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public ExerciseController(ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private ExerciseResponseDTO toDTO(Exercise exercise) {
        return new ExerciseResponseDTO(
            exercise.getExerciseId(),
            exercise.getUser().getUserId(),
            exercise.getName(),
            exercise.getMuscleGroup(),
            exercise.getIsSaved()
        );
    }

    @PostMapping
    public ExerciseResponseDTO createExercise(@Valid @RequestBody Exercise exercise) {
        User authenticatedUser = getAuthenticatedUser();
        exercise.setUser(authenticatedUser);
        Exercise saved = exerciseRepository.save(exercise);
        return toDTO(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> getExercise(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return exerciseRepository.findById(id)
                .map(exercise -> {
                    if (!exercise.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<ExerciseResponseDTO>build();
                    }
                    return ResponseEntity.ok(toDTO(exercise));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> updateExercise(@PathVariable UUID id, @Valid @RequestBody Exercise updatedExercise) {
        return exerciseRepository.findById(id)
                .map(existingExercise -> {
                    if(!existingExercise.getUser().getUserId().equals(getAuthenticatedUser().getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<ExerciseResponseDTO>build();
                    }
                    existingExercise.setName(updatedExercise.getName());
                    existingExercise.setMuscleGroup(updatedExercise.getMuscleGroup());
                    existingExercise.setIsSaved(updatedExercise.getIsSaved());

                    return ResponseEntity.ok(toDTO(exerciseRepository.save(existingExercise)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return exerciseRepository.findById(id)
                .map(exercise -> {
                    if(!exercise.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
                    }
                    exerciseRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }  
}
