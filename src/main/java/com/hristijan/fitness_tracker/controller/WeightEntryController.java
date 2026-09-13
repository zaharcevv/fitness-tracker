package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.entity.WeightEntry;
import com.hristijan.fitness_tracker.repository.WeightEntryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hristijan.fitness_tracker.repository.UserRepository;
import com.hristijan.fitness_tracker.entity.User;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import jakarta.validation.Valid;
import com.hristijan.fitness_tracker.dto.WeightEntryResponseDTO;
import com.hristijan.fitness_tracker.service.WeightEntryService;



@RestController
@RequestMapping("/weight-entries")
public class WeightEntryController {
    private final WeightEntryRepository weightEntryRepository;
    private final UserRepository userRepository;
    private final WeightEntryService weightEntryService;

    public WeightEntryController(WeightEntryRepository weightEntryRepository, UserRepository userRepository, WeightEntryService weightEntryService) {
        this.weightEntryRepository = weightEntryRepository;
        this.userRepository = userRepository;
        this.weightEntryService = weightEntryService;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private WeightEntryResponseDTO toDTO(WeightEntry weightEntry) {
        return new WeightEntryResponseDTO(
                weightEntry.getWeightEntryId(),
                weightEntry.getUser().getUserId(),
                weightEntry.getWeight(),
                weightEntry.getTime()
        );
    }

    @PostMapping
    public WeightEntryResponseDTO createWeightEntry(@Valid @RequestBody WeightEntry weightEntry) {
        User authenticatedUser = getAuthenticatedUser();
        weightEntry.setUser(authenticatedUser);
        WeightEntry saved = weightEntryRepository.save(weightEntry);
        return toDTO(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeightEntryResponseDTO> getWeightEntry(@PathVariable UUID id) {
        return weightEntryRepository.findById(id)
                .map(weightEntry -> {
                    if(!weightEntry.getUser().getUserId().equals(getAuthenticatedUser().getUserId())) {
                        return ResponseEntity.status(403).<WeightEntryResponseDTO>build();
                    }
                    return ResponseEntity.ok(toDTO(weightEntry));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeightEntryResponseDTO> updateWeightEntry(@PathVariable UUID id, @Valid @RequestBody WeightEntry updatedWeightEntry) {
        User authenticatedUser = getAuthenticatedUser();
        return weightEntryRepository.findById(id)
                .map(existingWeightEntry -> {
                    if(!existingWeightEntry.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<WeightEntryResponseDTO>build();
                    }
                    existingWeightEntry.setWeight(updatedWeightEntry.getWeight());
                    existingWeightEntry.setTime(updatedWeightEntry.getTime());
                    return ResponseEntity.ok(toDTO(weightEntryRepository.save(existingWeightEntry)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeightEntry(@PathVariable UUID id) {
        User authenticatedUser = getAuthenticatedUser();
        return weightEntryRepository.findById(id)
                .map(weightEntry -> {
                    if(!weightEntry.getUser().getUserId().equals(authenticatedUser.getUserId())) {
                        return ResponseEntity.status(403).<Void>build();
                    }
                    weightEntryRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/weight-trends")
    public ResponseEntity<Map<String, Object>> getWeightTrends(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        User authenticatedUser = getAuthenticatedUser();
        List<WeightEntry> entries = weightEntryRepository.findByUserAndDateRange(authenticatedUser.getUserId(), startDate, endDate);
        Map<String, Object> summary = weightEntryService.calculateWeightTrends(entries, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
}
