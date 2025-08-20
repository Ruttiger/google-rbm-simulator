package com.messi.rbm.authsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GoogleRbmSimApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleRbmSimApplication.class, args);
    }
}
