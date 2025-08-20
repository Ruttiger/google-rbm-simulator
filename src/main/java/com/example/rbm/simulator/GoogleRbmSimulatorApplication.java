package com.example.rbm.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
        "com.example.rbm.simulator",
        "com.messi.rbm.authsim"
})
@ConfigurationPropertiesScan(basePackages = "com.messi.rbm.authsim")
public class GoogleRbmSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleRbmSimulatorApplication.class, args);
    }
}
