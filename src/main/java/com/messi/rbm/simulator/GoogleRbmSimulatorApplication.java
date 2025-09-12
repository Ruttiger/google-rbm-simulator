package com.messi.rbm.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for the RBM simulator application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class GoogleRbmSimulatorApplication {
    public static void main(final String[] args) {
        SpringApplication.run(GoogleRbmSimulatorApplication.class, args);
    }
}
