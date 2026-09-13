package com.hristijan.fitness_tracker.service;

import com.hristijan.fitness_tracker.entity.Food;
import com.hristijan.fitness_tracker.entity.FoodEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoodEntryServiceTest {

    private final FoodEntryService foodEntryService = new FoodEntryService();

    private Food makeFood(int calories, int protein, int carbs, int fats) {
        Food food = new Food();
        food.setCalories(calories);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFats(fats);
        return food;
    }

    private FoodEntry makeEntry(Food food, int quantity, LocalDate date) {
        FoodEntry entry = new FoodEntry();
        entry.setFood(food);
        entry.setQuantity(quantity);
        entry.setTime(date);
        return entry;
    }

    @org.junit.jupiter.api.Test
    void calculateDailySummary_calculatesCorrectTotals() {
        Food food = makeFood(200, 20, 30, 10);
        FoodEntry entry = makeEntry(food, 3, LocalDate.of(2026, 9, 6));

        Map<String, Object> result = foodEntryService.calculateDailySummary(List.of(entry));

        assertEquals(600, result.get("totalCalories"));
        assertEquals(60, result.get("totalProtein"));
        assertEquals(90, result.get("totalCarbs"));
        assertEquals(30, result.get("totalFats"));
    }

    @org.junit.jupiter.api.Test
    void calculateDailySummary_returnsZeroForEmptyList() {
        Map<String, Object> result = foodEntryService.calculateDailySummary(List.of());

        assertEquals(0, result.get("totalCalories"));
        assertEquals(0, result.get("totalProtein"));
        assertEquals(0, result.get("totalCarbs"));
        assertEquals(0, result.get("totalFats"));
    }

    @org.junit.jupiter.api.Test
    void calculateDailySummary_sumsMultipleEntriesCorrectly() {
        Food breakfast = makeFood(300, 15, 40, 10);
        Food lunch = makeFood(500, 30, 50, 20);

        List<FoodEntry> entries = List.of(
                makeEntry(breakfast, 1, LocalDate.of(2026, 9, 6)),
                makeEntry(lunch, 2, LocalDate.of(2026, 9, 6))
        );

        Map<String, Object> result = foodEntryService.calculateDailySummary(entries);

        assertEquals(1300, result.get("totalCalories"));
        assertEquals(75, result.get("totalProtein"));
        assertEquals(140, result.get("totalCarbs"));
        assertEquals(50, result.get("totalFats"));
    }

    
}