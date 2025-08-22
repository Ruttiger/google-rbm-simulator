package com.messi.rbm.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.messi.rbm")
@ConfigurationPropertiesScan
public class GoogleRbmSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleRbmSimulatorApplication.class, args);
    }
}
