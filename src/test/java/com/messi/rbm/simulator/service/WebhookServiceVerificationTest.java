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
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

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

    @Test
    void acceptsAndRegistersOnSecretEcho() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.setDispatcher(new Dispatcher() {
                @Override
                public MockResponse dispatch(RecordedRequest request) {
                    String body = request.getBody().readUtf8();
                    return new MockResponse()
                            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(body);
                }
            });
            server.start();
            String url = server.url("/callback").toString();

            WebhookService service = new WebhookService(WebClient.builder());
            boolean ok = service.verifyAndRegister("ag", url, "token").block();
            assertThat(ok).isTrue();
            assertThat(service.getConfig("ag").block().webhookUrl()).isEqualTo(url);
        }
    }
}

