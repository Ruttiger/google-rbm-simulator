package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.WebhookConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookServiceVerificationTest {

    @Test
    void rejectsOnChallengeMismatch() {
        String secret = "fixed";
        ExchangeFunction fn = request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"secret\":\"other\"}")
                .build());
        WebhookService service = new WebhookService(WebClient.builder().exchangeFunction(fn));
        boolean ok = service.verifyAndRegister("ag", "http://example", "token").block();
        assertThat(ok).isFalse();
        assertThat(service.getConfig("ag").blockOptional()).isEmpty();
    }
}

