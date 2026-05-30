package com.multimediareview.controller;

import com.multimediareview.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> listUsers(@RequestParam(required = false) String role) {
        var users = role != null
                ? userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(role))
                    .collect(Collectors.toList())
                : userRepository.findAll();
        return ResponseEntity.ok(users.stream().map(u -> Map.of(
                "id", (Object) u.getId(),
                "username", u.getUsername(),
                "name", u.getName(),
                "role", u.getRole().name()
        )).collect(Collectors.toList()));
    }
}
