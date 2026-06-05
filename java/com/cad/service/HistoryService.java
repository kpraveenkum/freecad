package com.cad.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class HistoryService {
    
    @Autowired
    private AmazonDynamoDB dynamoDBClient;
    
    private DynamoDB dynamoDB;
    private Table table;
    
    @PostConstruct
    public void init() {
        this.dynamoDB = new DynamoDB(dynamoDBClient);
        try {
            this.table = dynamoDB.getTable("ValidationHistory");
            System.out.println("✅ Connected to DynamoDB table: ValidationHistory");
        } catch (Exception e) {
            System.err.println("⚠️ Table ValidationHistory not found: " + e.getMessage());
        }
    }
    
    public void saveValidation(Map<String, Object> validationResult, String designName) {
        try {
            String validationId = UUID.randomUUID().toString();
            String timestamp = new Date().toString();
            
            // Convert violations to string safely
            String violationsStr = "[]";
            Object violations = validationResult.getOrDefault("violations", "[]");
            if (violations instanceof List) {
                violationsStr = violations.toString();
            } else {
                violationsStr = String.valueOf(violations);
            }
            
            Item item = new Item()
                .withPrimaryKey("validation_id", validationId)
                .withString("timestamp", timestamp)
                .withString("design_name", designName)
                .withInt("score", (Integer) validationResult.getOrDefault("score", 0))
                .withString("status", (String) validationResult.getOrDefault("status", "UNKNOWN"))
                .withInt("violations_count", (Integer) validationResult.getOrDefault("violations_count", 0))
                .withString("violations", violationsStr)
                .withString("summary", (String) validationResult.getOrDefault("summary", ""))
                .withLong("ttl", System.currentTimeMillis() / 1000 + 7776000);
            
            if (table != null) {
                table.putItem(item);
                System.out.println("✅ Saved to DynamoDB: " + validationId);
            } else {
                System.out.println("⚠️ DynamoDB not available, skipping save");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to save to DynamoDB: " + e.getMessage());
        }
    }
}
