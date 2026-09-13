package com.hristijan.fitness_tracker.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.hristijan.fitness_tracker.entity.WeightEntry;

@Service
public class WeightEntryService {
    
    public Map<String, Object> calculateWeightTrends(List<WeightEntry> entries, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> weightByDay = new HashMap<>();

        if (entries.isEmpty()) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("startDate", startDate);
            summary.put("endDate", endDate);
            summary.put("daysLogged", 0);
            summary.put("averageWeights", null);
            summary.put("startWeight", null);
            summary.put("endWeight", null);
            summary.put("difference", null);
            return summary;
        }

        LocalDate firstLoggedDay = endDate;
        LocalDate lastLoggedDay = startDate;

        Set<LocalDate> daysLogged = new HashSet<>();
        for(WeightEntry entry : entries) {
            LocalDate day = entry.getTime();
            if(day.isBefore(firstLoggedDay)) {
                firstLoggedDay = day;
            }
            if(day.isAfter(lastLoggedDay)) {
                lastLoggedDay = day;
            }
            daysLogged.add(day);
        }

        for(LocalDate day : daysLogged) {
            Double sum = 0.0;
            int numOfEntries = 0;
            for(WeightEntry entry : entries) {
                if(entry.getTime().equals(day)) {
                    sum += entry.getWeight();
                    numOfEntries = numOfEntries + 1;
                }
            }

            Double dailyAverage = sum / numOfEntries;
            weightByDay.put(day, dailyAverage);
        }

        Double startWeight = weightByDay.get(firstLoggedDay);
        Double endWeight = weightByDay.get(lastLoggedDay);
        Double difference = endWeight - startWeight;

        Map<String, Object> summary = new HashMap<>();
        summary.put("startDate", startDate);
        summary.put("endDate", endDate);
        summary.put("daysLogged", daysLogged.size());
        summary.put("averageWeights", weightByDay);
        summary.put("startWeight", startWeight);
        summary.put("endWeight", endWeight);
        summary.put("difference", difference);
        return summary;
    }
}
