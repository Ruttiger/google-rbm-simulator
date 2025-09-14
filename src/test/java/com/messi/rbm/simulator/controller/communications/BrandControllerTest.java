package com.messi.rbm.simulator.controller.communications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Map;

/** Tests for BrandController authentication and creation. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@org.springframework.test.context.TestPropertySource(properties = "auth.mode=STRICT")
class BrandControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private String obtainToken() {
        return webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&client_id=test-client&client_secret=secret")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult().getResponseBody().get("access_token").toString();
    }

    @Test
    void createBrandRequiresToken() {
        webTestClient.post().uri("/v1/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("displayName", "Test"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createBrandSuccess() {
        String token = obtainToken();
        webTestClient.post().uri("/v1/brands")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("displayName", "Brand"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").exists();
    }
}
