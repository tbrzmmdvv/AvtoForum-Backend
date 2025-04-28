package com.example.avtoforum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AvtoforumApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvtoforumApplication.class, args);
    }
}
