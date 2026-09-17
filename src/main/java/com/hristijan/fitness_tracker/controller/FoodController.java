package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.entity.Food;
import com.hristijan.fitness_tracker.repository.FoodRepository;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import com.hristijan.fitness_tracker.entity.User;
import com.hristijan.fitness_tracker.repository.UserRepository;
import com.hristijan.fitness_tracker.dto.FoodResponseDTO;


@RestController
@RequestMapping("/foods")
public class FoodController {
    
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public FoodController(FoodRepository foodRepository, UserRepository userRepository) {
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private FoodResponseDTO toDTO(Food food) {
        return new FoodResponseDTO(
                food.getFoodId(),
                food.getUser().getUserId(),
                food.getName(),
                food.getCalories(),
                food.getProtein(),
                food.getCarbs(),
                food.getFats(),
                food.getIsSaved()
        );
    }

    @PostMapping
    public FoodResponseDTO createFood(@Valid @RequestBody Food food) {
        User authenticatedUser = getAuthenticatedUser();
        food.setUser(authenticatedUser);
        Food saved = foodRepository.save(food);
        return toDTO(saved);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> getFood(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return foodRepository.findById(id)
                .map(food -> {
                    if (!food.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<FoodResponseDTO>build();
                    }
                    return ResponseEntity.ok(toDTO(food));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Food>> getFoods() {
        User authenticatedUser = getAuthenticatedUser();

        List<Food> foods = foodRepository.findByUser(authenticatedUser.getUserId());

        return ResponseEntity.ok(foods);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> updateFood(@PathVariable UUID id, @Valid @RequestBody Food updatedFood) {
        User authenticatedUser = getAuthenticatedUser();
        return foodRepository.findById(id)
                .map(existingFood -> {
                    if (!existingFood.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<FoodResponseDTO>build();
                    }
                    existingFood.setName(updatedFood.getName());
                    existingFood.setCalories(updatedFood.getCalories());
                    existingFood.setProtein(updatedFood.getProtein());
                    existingFood.setCarbs(updatedFood.getCarbs());
                    existingFood.setFats(updatedFood.getFats());
                    existingFood.setIsSaved(updatedFood.getIsSaved());
                    return ResponseEntity.ok(toDTO(foodRepository.save(existingFood)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return foodRepository.findById(id)
                .map(food -> {
                    if (!food.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
                    }
                    foodRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
