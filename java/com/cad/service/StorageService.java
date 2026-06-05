package com.cad.service;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StorageService {
    
    @Autowired
    private AmazonS3 s3Client;
    
    @Autowired
    private String bucketName;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    public String saveValidationReport(Map<String, Object> result, String designName) {
        try {
            String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportKey = String.format("reports/%s/%s_validation_%s.json", 
                date, designName, timestamp);
            
            String json = mapper.writeValueAsString(result);
            
            s3Client.putObject(bucketName, reportKey, json);
            System.out.println("✅ Report saved to S3: " + reportKey);
            
            return reportKey;
        } catch (Exception e) {
            System.err.println("❌ Failed to save report to S3: " + e.getMessage());
            return null;
        }
    }
    
    public void saveRules(Map<String, Object> rules) {
        try {
            String rulesKey = "rules/design_rules.json";
            String json = mapper.writeValueAsString(rules);
            s3Client.putObject(bucketName, rulesKey, json);
            System.out.println("✅ Rules saved to S3: " + rulesKey);
        } catch (Exception e) {
            System.err.println("❌ Failed to save rules to S3: " + e.getMessage());
        }
    }
    
    public String getRulesFromS3() {
        try {
            String rulesKey = "rules/design_rules.json";
            if (s3Client.doesObjectExist(bucketName, rulesKey)) {
                return s3Client.getObjectAsString(bucketName, rulesKey);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to get rules from S3: " + e.getMessage());
        }
        return null;
    }
}
