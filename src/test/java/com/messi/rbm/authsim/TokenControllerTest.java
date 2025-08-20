package com.messi.rbm.authsim;

import com.example.rbm.simulator.GoogleRbmSimulatorApplication;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Base64;

@SpringBootTest(classes = GoogleRbmSimulatorApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TokenControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void clientCredentialsGrantReturnsToken() {
        webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, basic("test-client", "secret"))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("scope", "rbm.bots"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").exists();
    }

    @Test
    void jwtBearerGrantReturnsToken() throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("bot@example.com").build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner("01234567890123456789012345678901"));

        webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                        .with("assertion", jwt.serialize())
                        .with("scope", "rbm.messages"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").exists();
    }

    private String basic(String id, String secret) {
        return "Basic " + Base64.getEncoder().encodeToString((id + ":" + secret).getBytes());
    }
}
