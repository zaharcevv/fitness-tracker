package com.hristijan.fitness_tracker.controller;

import com.hristijan.fitness_tracker.dto.UserResponseDTO;
import com.hristijan.fitness_tracker.entity.User;
import com.hristijan.fitness_tracker.repository.UserRepository;
import com.hristijan.fitness_tracker.service.JwtService;

import jakarta.validation.Valid;

import com.hristijan.fitness_tracker.dto.RegisterRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        return new UserResponseDTO(
                saved.getUserId(),
                saved.getUsername(),
                saved.getDailyCalorieTarget(),
                saved.getDailyProteinTarget(),
                saved.getDailyCarbsTarget(),
                saved.getDailyFatsTarget()
        );
    }


    @PostMapping("/login")
    public String login(@RequestBody RegisterRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return jwtService.generateToken(user.getUsername());
    }
}