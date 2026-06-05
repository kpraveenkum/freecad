package com.cad.service;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class RuleService {
    
    @Autowired
    private AmazonS3 s3Client;
    
    @Autowired
    private String bucketName;
    
    private Map<String, Object> rules = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();
    
    @PostConstruct
    public void loadRules() {
        try {
            String rulesKey = "rules/design_rules.json";
            
            if (s3Client.doesObjectExist(bucketName, rulesKey)) {
                String content = s3Client.getObjectAsString(bucketName, rulesKey);
                rules = mapper.readValue(content, Map.class);
                System.out.println("✅ Loaded " + rules.size() + " rules from S3");
                System.out.println("   Rules: " + rules.keySet());
            } else {
                System.out.println("⚠️ No rules file found in S3, using defaults");
                createDefaultRules();
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load rules from S3: " + e.getMessage());
            createDefaultRules();
        }
    }
    
    private void createDefaultRules() {
        rules.put("min_wall_thickness", 1.5);
        rules.put("max_wall_thickness", 50.0);
        rules.put("min_hole_diameter", 2.0);
        rules.put("max_aspect_ratio", 10.0);
        rules.put("max_features", 50);
        rules.put("min_fillet_radius", 0.5);
        System.out.println("✅ Using default rules");
    }
    
    public Map<String, Object> getRules() {
        return rules;
    }
    
    public double getMinWallThickness() {
        Object value = rules.getOrDefault("min_wall_thickness", 1.5);
        return ((Number) value).doubleValue();
    }
    
    public double getMaxWallThickness() {
        Object value = rules.getOrDefault("max_wall_thickness", 50.0);
        return ((Number) value).doubleValue();
    }
    
    public double getMinHoleDiameter() {
        Object value = rules.getOrDefault("min_hole_diameter", 2.0);
        return ((Number) value).doubleValue();
    }
    
    public double getMaxAspectRatio() {
        Object value = rules.getOrDefault("max_aspect_ratio", 10.0);
        return ((Number) value).doubleValue();
    }
    
    public int getMaxFeatures() {
        Object value = rules.getOrDefault("max_features", 50);
        return ((Number) value).intValue();
    }
}
