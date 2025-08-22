package com.messi.rbm.simulator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.messi.rbm.simulator.service.JwtService;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@org.springframework.test.context.TestPropertySource(properties = {
        "auth.mode=STRICT",
        "auth.accepted-clients[0].client-id=test-client",
        "auth.accepted-clients[0].client-secret=secret",
        "auth.allowed-scopes=rbm.bots,rbm.messages"
})
class TokenControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtService jwtService;

    @Test
    void clientCredentialsGrantReturnsToken() throws Exception {
        doReturn("token").when(jwtService).generateToken(anyString(), anyList());
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
    void clientCredentialsGrantRejectsInvalidClient() throws Exception {
        doReturn("token").when(jwtService).generateToken(anyString(), anyList());
        webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, basic("bad", "wrong"))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void clientCredentialsGrantRejectsMalformedHeader() throws Exception {
        doReturn("token").when(jwtService).generateToken(anyString(), anyList());
        webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic !!!")
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void clientCredentialsGrantRejectsHeaderWithoutColon() throws Exception {
        doReturn("token").when(jwtService).generateToken(anyString(), anyList());
        String malformed = Base64.getEncoder().encodeToString("nocolon".getBytes(StandardCharsets.UTF_8));
        webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + malformed)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void jwtBearerGrantReturnsToken() throws Exception {
        doReturn("token").when(jwtService).generateToken(anyString(), anyList());
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

    @Test
    void tokenGenerationFailureReturnsServerError() throws Exception {
        doThrow(new RuntimeException("boom")).when(jwtService).generateToken(anyString(), anyList());
        webTestClient.post().uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, basic("test-client", "secret"))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private String basic(String id, String secret) {
        return "Basic " + Base64.getEncoder().encodeToString((id + ":" + secret).getBytes());
    }
}
