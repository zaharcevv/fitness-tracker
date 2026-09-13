package com.hristijan.fitness_tracker.service;

import com.hristijan.fitness_tracker.entity.FoodEntry;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FoodEntryService {

    public Map<String, Object> calculateDailySummary(List<FoodEntry> entries) {
        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFats = 0;

        for (FoodEntry entry : entries) {
            int quantity = entry.getQuantity();
            totalCalories += entry.getFood().getCalories() * quantity;
            totalProtein += entry.getFood().getProtein() * quantity;
            totalCarbs += entry.getFood().getCarbs() * quantity;
            totalFats += entry.getFood().getFats() * quantity;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCalories", totalCalories);
        result.put("totalProtein", totalProtein);
        result.put("totalCarbs", totalCarbs);
        result.put("totalFats", totalFats);
        return result;
    }

    public Map<String, Object> calculateWeeklySummary(List<FoodEntry> entries) {
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        Map<LocalDate, Integer> proteinByDay = new HashMap<>();
        Map<LocalDate, Integer> carbsByDay = new HashMap<>();
        Map<LocalDate, Integer> fatsByDay = new HashMap<>();

        for (FoodEntry entry : entries) {
            LocalDate day = entry.getTime();
            int quantity = entry.getQuantity();

            caloriesByDay.merge(day, entry.getFood().getCalories() * quantity, Integer::sum);
            proteinByDay.merge(day, entry.getFood().getProtein() * quantity, Integer::sum);
            carbsByDay.merge(day, entry.getFood().getCarbs() * quantity, Integer::sum);
            fatsByDay.merge(day, entry.getFood().getFats() * quantity, Integer::sum);
        }

        int daysLogged = caloriesByDay.size();

        double avgCalories = daysLogged == 0 ? 0 : caloriesByDay.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgProtein = daysLogged == 0 ? 0 : proteinByDay.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgCarbs = daysLogged == 0 ? 0 : carbsByDay.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgFats = daysLogged == 0 ? 0 : fatsByDay.values().stream().mapToInt(Integer::intValue).average().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("daysLogged", daysLogged);
        result.put("avgCalories", avgCalories);
        result.put("avgProtein", avgProtein);
        result.put("avgCarbs", avgCarbs);
        result.put("avgFats", avgFats);
        return result;
    }
}