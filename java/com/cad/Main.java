package com.cad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class Main {
    
    // Cache for recent validations
    private static final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL = 5000; // 5 seconds
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("\n✅ Optimized CAD Validation Service Started!\n");
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("cache_size", cache.size());
        return status;
    }
    
    @PostMapping("/validate")
    public Map<String, Object> validate(@RequestBody Map<String, Object> request) {
        long startTime = System.currentTimeMillis();
        
        String designName = (String) request.getOrDefault("name", "Unknown");
        String cacheKey = designName + request.get("feature_count") + request.get("min_thickness");
        
        // Check cache
        if (cache.containsKey(cacheKey)) {
            Map<String, Object> cached = cache.get(cacheKey);
            long cacheTime = (long) cached.getOrDefault("cache_time", 0L);
            if (System.currentTimeMillis() - cacheTime < CACHE_TTL) {
                Map<String, Object> response = new HashMap<>(cached);
                response.put("cached", true);
                response.put("response_time_ms", System.currentTimeMillis() - startTime);
                return response;
            }
        }
        
        // Fast validation
        Number minThickness = (Number) request.get("min_thickness");
        Number featureCount = (Number) request.get("feature_count");
        
        List<Map<String, Object>> violations = new ArrayList<>();
        
        // Quick rule checks
        if (minThickness != null && minThickness.doubleValue() < 1.5) {
            Map<String, Object> v = new HashMap<>();
            v.put("rule_id", "WALL_001");
            v.put("severity", "CRITICAL");
            v.put("message", "Wall thickness below 1.5mm");
            v.put("suggested_fix", "Increase thickness");
            v.put("object_ids", new ArrayList<>());
            violations.add(v);
        }
        
        if (featureCount != null && featureCount.intValue() > 50) {
            Map<String, Object> w = new HashMap<>();
            w.put("rule_id", "COMPLEX_001");
            w.put("severity", "WARNING");
            w.put("message", "Too many features");
            w.put("suggested_fix", "Simplify design");
            w.put("object_ids", new ArrayList<>());
            violations.add(w);
        }
        
        int score = 100 - (violations.size() * 25);
        score = Math.max(0, Math.min(100, score));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", violations.isEmpty());
        response.put("score", score);
        response.put("status", violations.isEmpty() ? "PASSED" : "FAILED");
        response.put("violations_count", violations.size());
        response.put("violations", violations);
        response.put("response_time_ms", System.currentTimeMillis() - startTime);
        response.put("cache_time", System.currentTimeMillis());
        
        // Cache response
        cache.put(cacheKey, response);
        
        System.out.println("✅ " + designName + " | Score: " + score + " | Time: " + (System.currentTimeMillis() - startTime) + "ms");
        
        return response;
    }
}
