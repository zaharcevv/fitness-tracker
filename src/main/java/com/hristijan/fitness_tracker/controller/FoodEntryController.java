package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.entity.FoodEntry;
import com.hristijan.fitness_tracker.repository.FoodEntryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import com.hristijan.fitness_tracker.entity.Food;
import com.hristijan.fitness_tracker.entity.User;
import com.hristijan.fitness_tracker.dto.FoodEntryResponseDTO;
import com.hristijan.fitness_tracker.repository.UserRepository;
import com.hristijan.fitness_tracker.repository.FoodRepository;
import com.hristijan.fitness_tracker.service.FoodEntryService;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.List;



@RestController
@RequestMapping("/food-entries")
public class FoodEntryController {
    private final FoodEntryRepository foodEntryRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final FoodEntryService foodEntryService;

    public FoodEntryController(FoodEntryRepository foodEntryRepository, UserRepository userRepository, FoodRepository foodRepository, FoodEntryService foodEntryService) {
        this.foodEntryRepository = foodEntryRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.foodEntryService = foodEntryService;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private FoodEntryResponseDTO toDTO(FoodEntry foodEntry) {
        Food food = foodEntry.getFood();
        User user = foodEntry.getUser();
        return new FoodEntryResponseDTO(
            foodEntry.getFoodEntryId(),
            food.getFoodId(),
            user.getUserId(),
            food.getName(),
            food.getCalories(),
            food.getProtein(),
            food.getCarbs(),
            food.getFats(),
            food.getIsSaved(),
            foodEntry.getQuantity(),
            foodEntry.getTime()
        );
    }

    @PostMapping
    public FoodEntryResponseDTO createFoodEntry(@Valid @RequestBody FoodEntry foodEntry) {
        User authenticatedUser = getAuthenticatedUser();

        Food actualFood = foodRepository.findById(foodEntry.getFood().getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (!actualFood.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new RuntimeException("Cannot log a food entry for a food you do not own");
        }

        foodEntry.setFood(actualFood);
        foodEntry.setUser(authenticatedUser);
        FoodEntry saved = foodEntryRepository.save(foodEntry);
        return toDTO(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodEntryResponseDTO> getFoodEntry(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return foodEntryRepository.findById(id)
                .map(foodEntry -> {
                    if (!foodEntry.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<FoodEntryResponseDTO>build();
                    }
                    return ResponseEntity.ok(toDTO(foodEntry));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodEntryResponseDTO> updateFoodEntry(@PathVariable UUID id, @Valid @RequestBody FoodEntry updatedFoodEntry) {
        User authenticatedUser = getAuthenticatedUser();
        Food actualFood = foodRepository.findById(updatedFoodEntry.getFood().getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));
        updatedFoodEntry.setFood(actualFood);
        return foodEntryRepository.findById(id)
                .map(existingFoodEntry -> {
                    if (!existingFoodEntry.getUser().getUserId().equals(authenticatedUser.getUserId()) || !updatedFoodEntry.getFood().getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<FoodEntryResponseDTO>build();
                    }
                    existingFoodEntry.setFood(updatedFoodEntry.getFood());
                    existingFoodEntry.setQuantity(updatedFoodEntry.getQuantity());
                    existingFoodEntry.setTime(updatedFoodEntry.getTime());
                    return ResponseEntity.ok(toDTO(foodEntryRepository.save(existingFoodEntry)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodEntry(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return foodEntryRepository.findById(id)
        .map(foodEntry -> {
            if(!foodEntry.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
            }
            foodEntryRepository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        })
        .orElse(ResponseEntity.notFound().build());
    }

   @GetMapping("/daily-summary")
    public ResponseEntity<Map<String, Object>> getDailySummary(@RequestParam LocalDate date) {
        User authenticatedUser = getAuthenticatedUser();

        List<FoodEntry> entries = foodEntryRepository.findByUserAndDate(authenticatedUser.getUserId(), date);
        Map<String, Object> summary = foodEntryService.calculateDailySummary(entries);

        summary.put("date", date);
        summary.put("calorieTarget", authenticatedUser.getDailyCalorieTarget());
        summary.put("proteinTarget", authenticatedUser.getDailyProteinTarget());
        summary.put("carbsTarget", authenticatedUser.getDailyCarbsTarget());
        summary.put("fatsTarget", authenticatedUser.getDailyFatsTarget());

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<Map<String, Object>> getWeeklySummary(@RequestParam LocalDate weekStart, @RequestParam LocalDate weekEnd) {
        User authenticatedUser = getAuthenticatedUser();

        List<FoodEntry> entries = foodEntryRepository.findByUserAndDateRange(authenticatedUser.getUserId(), weekStart, weekEnd);
        Map<String, Object> summary = foodEntryService.calculateWeeklySummary(entries);

        summary.put("weekStart", weekStart);
        summary.put("weekEnd", weekEnd);
        summary.put("calorieTarget", authenticatedUser.getDailyCalorieTarget());
        summary.put("proteinTarget", authenticatedUser.getDailyProteinTarget());
        summary.put("carbsTarget", authenticatedUser.getDailyCarbsTarget());
        summary.put("fatsTarget", authenticatedUser.getDailyFatsTarget());

        return ResponseEntity.ok(summary);
    }
}
