package com.prm392.be.labverse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${aws.region.static}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() {
        Region region = Region.of(awsRegion);
        return S3Client.builder()
                .region(region)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        Region region = Region.of(awsRegion);
        return S3Presigner.builder()
                .region(region)
                .build();
    }
}