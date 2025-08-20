package com.example.rbm.simulator;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class ContainerIntegrationIT {

    @Container
    static GenericContainer<?> container = new GenericContainer<>("alpine:3.18")
            .withCommand("sleep 60");

    @Test
    void containerIsRunning() {
        assertTrue(container.isRunning());
    }
}
