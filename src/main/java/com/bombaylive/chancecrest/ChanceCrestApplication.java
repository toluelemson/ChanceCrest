package com.bombaylive.chancecrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * The main entry point for the ChanceCrest Spring Boot application.
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ChanceCrestApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args Command line arguments, if any.
     */
    public static void main(String[] args) {
        SpringApplication.run(ChanceCrestApplication.class, args);
    }

}