package win.agus4the.rbm.simulator.controller.messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import win.agus4the.rbm.simulator.GoogleRbmSimulatorApplication;
import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GoogleRbmSimulatorApplication.class)
@AutoConfigureWebTestClient
@Import(UserControllerTest.RandomTestConfig.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BusinessMessagingService messagingService;

    @Autowired
    private Supplier<Random> randomSupplier;

    private static final String AGENT = "AGENT";

    @AfterEach
    void tearDown() {
        messagingService.reset();
    }

    @Test
    void lessThan500NumbersReturnsBadRequest() {
        List<String> users = numbers(499);
        webTestClient.post().uri("/v1/users:batchGet")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("agentId", AGENT, "users", users))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void moreThan10000NumbersReturnsBadRequest() {
        List<String> users = numbers(10_001);
        webTestClient.post().uri("/v1/users:batchGet")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("agentId", AGENT, "users", users))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void duplicateNumbersReturnBadRequest() {
        List<String> users = new ArrayList<>(numbers(500));
        users.set(1, users.get(0));
        webTestClient.post().uri("/v1/users:batchGet")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("agentId", AGENT, "users", users))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void validRequestReturnsSampleCounts() {
        List<String> users = numbers(500);
        messagingService.addTester(AGENT, users.get(0)).block();
        messagingService.addTester(AGENT, users.get(1)).block();

        int sampleSize = (int) Math.ceil(users.size() * 0.75);
        List<String> sample = new ArrayList<>(users);
        Collections.shuffle(sample, randomSupplier.get());
        sample = sample.subList(0, sampleSize);
        Set<String> testers = Set.of(users.get(0), users.get(1));
        long reachableInSample = sample.stream().filter(testers::contains).count();

        webTestClient.post().uri("/v1/users:batchGet")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("agentId", AGENT, "users", users))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.reachableUsers.length()").isEqualTo(2)
                .jsonPath("$.totalRandomSampleUserCount").isEqualTo(sampleSize)
                .jsonPath("$.reachableRandomSampleUserCount").isEqualTo((int) reachableInSample);
    }

    private List<String> numbers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> String.format("+1%010d", i))
                .toList();
    }

    @TestConfiguration
    static class RandomTestConfig {
        @Bean
        Supplier<Random> deterministicRandomSupplier() {
            return () -> new Random(0);
        }
    }
}
