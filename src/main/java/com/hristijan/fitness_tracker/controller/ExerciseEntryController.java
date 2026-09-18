package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.entity.ExerciseEntry;
import com.hristijan.fitness_tracker.repository.ExerciseEntryRepository;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.hristijan.fitness_tracker.dto.ExerciseEntryResponseDTO;
import com.hristijan.fitness_tracker.entity.Exercise;
import com.hristijan.fitness_tracker.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hristijan.fitness_tracker.repository.UserRepository;
import com.hristijan.fitness_tracker.repository.ExerciseRepository;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;




@RestController 
@RequestMapping("/exercise-entries")
public class ExerciseEntryController {
    private final ExerciseEntryRepository exerciseEntryRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public ExerciseEntryController(ExerciseEntryRepository exerciseEntryRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.exerciseEntryRepository = exerciseEntryRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private ExerciseEntryResponseDTO toDTO(ExerciseEntry exerciseEntry) {
        Exercise exercise = exerciseEntry.getExercise();
        return new ExerciseEntryResponseDTO(
            exerciseEntry.getExerciseEntryId(),
            exercise.getExerciseId(),
            exerciseEntry.getUser().getUserId(),
            exercise.getName(),
            exercise.getMuscleGroup(),
            exercise.getIsSaved(),
            exerciseEntry.getSets(),
            exerciseEntry.getReps(),
            exerciseEntry.getWeight(),
            exerciseEntry.getTime()
        );
    }

    @PostMapping
    public ExerciseEntryResponseDTO createExerciseEntry(@Valid @RequestBody ExerciseEntry exerciseEntry) {
        User authenticatedUser = getAuthenticatedUser();

        Exercise actualExercise = exerciseRepository.findById(exerciseEntry.getExercise().getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
               
                
        if(!actualExercise.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new RuntimeException("Cannot log an exercise entry for an exercise you do not own");
        }

        exerciseEntry.setExercise(actualExercise);
        exerciseEntry.setUser(authenticatedUser);
        ExerciseEntry saved = exerciseEntryRepository.save(exerciseEntry);
        return toDTO(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseEntryResponseDTO> getExerciseEntry(@PathVariable UUID id) {
        return exerciseEntryRepository.findById(id)
                .map(exerciseEntry -> {
                    if(!exerciseEntry.getUser().getUserId().equals(getAuthenticatedUser().getUserId())) {
                        return ResponseEntity.status(403).<ExerciseEntryResponseDTO>build();
                    }
                    return ResponseEntity.ok(toDTO(exerciseEntry));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseEntryResponseDTO> updateExerciseEntry(@PathVariable UUID id, @Valid @RequestBody ExerciseEntry updatedExerciseEntry) {
        User authenticatedUser = getAuthenticatedUser();
        Exercise actualExercise = exerciseRepository.findById(updatedExerciseEntry.getExercise().getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        updatedExerciseEntry.setExercise(actualExercise);
        return exerciseEntryRepository.findById(id)
                .map(existingExerciseEntry -> {
                    if(!existingExerciseEntry.getUser().getUserId().equals(authenticatedUser.getUserId()) || !updatedExerciseEntry.getExercise().getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<ExerciseEntryResponseDTO>build();
                    }
                    existingExerciseEntry.setExercise(updatedExerciseEntry.getExercise());
                    existingExerciseEntry.setSets(updatedExerciseEntry.getSets());
                    existingExerciseEntry.setReps(updatedExerciseEntry.getReps());
                    existingExerciseEntry.setWeight(updatedExerciseEntry.getWeight());
                    existingExerciseEntry.setTime(updatedExerciseEntry.getTime());

                    return ResponseEntity.ok(toDTO(exerciseEntryRepository.save(existingExerciseEntry)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseEntry(@PathVariable UUID id) {
       User authenticatedUser = getAuthenticatedUser();
        return exerciseEntryRepository.findById(id)
                .map(exerciseEntry -> {
                    if(!exerciseEntry.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<Void>build();
                    }
                    exerciseEntryRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }  
}
