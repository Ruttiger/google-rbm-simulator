package com.messi.rbm.authsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AuthSimApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthSimApplication.class, args);
    }
}
