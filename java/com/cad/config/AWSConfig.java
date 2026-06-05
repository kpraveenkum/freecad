package com.cad.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
    
    private static final String REGION = "us-east-1";
    private static final String BUCKET_NAME = "cad-validation-bucket-375209111033";
    
    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }
    
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }
    
    @Bean
    public String bucketName() {
        return BUCKET_NAME;
    }
}
