package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.entity.User;
import com.hristijan.fitness_tracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.hristijan.fitness_tracker.dto.UserResponseDTO;
import com.hristijan.fitness_tracker.dto.UserUpdateRequestDTO;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getDailyCalorieTarget(),
                user.getDailyProteinTarget(),
                user.getDailyCarbsTarget(),
                user.getDailyFatsTarget()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return userRepository.findById(id)
               .map(user -> {
                    if(!user.getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<UserResponseDTO>build();
                    }
                    return ResponseEntity.ok(toDTO(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser() {
        User authenticatedUser = getAuthenticatedUser();
        return toDTO(authenticatedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDTO updatedUser) {
        User authenticatedUser = getAuthenticatedUser();
        return userRepository.findById(id)
                .map(existingUser -> {
                    if(!existingUser.getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<UserResponseDTO>build();
                    }
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setDailyCalorieTarget(updatedUser.getDailyCalorieTarget());
                    existingUser.setDailyProteinTarget(updatedUser.getDailyProteinTarget());
                    existingUser.setDailyCarbsTarget(updatedUser.getDailyCarbsTarget());
                    existingUser.setDailyFatsTarget(updatedUser.getDailyFatsTarget());
                    return ResponseEntity.ok(toDTO(userRepository.save(existingUser)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
       User authenticatedUser = getAuthenticatedUser();
        return userRepository.findById(id)
                .map(user -> {
                    if(!user.getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<Void>build();
                    }
                    userRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}