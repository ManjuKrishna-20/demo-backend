package com.codework.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "service", "demo-backend",
                "status", "UP",
                "timestamp", Instant.now().toString()
        );
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "OK");
    }
}
