package win.agus4the.rbm.simulator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=RBM"
})
@AutoConfigureWebTestClient
class InterfaceSelectionRbmOnlyIntegrationTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void hidesPcmEndpointsWhenDisabled() {
        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit").exchange().expectStatus().isNotFound();
    }
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=PCM"
})
@AutoConfigureWebTestClient
class InterfaceSelectionPcmOnlyIntegrationTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void hidesRbmEndpointsWhenDisabled() {
        webTestClient.post().uri("/v1/phones/111/agentMessages?agentId=a&messageId=b").exchange().expectStatus().isNotFound();
    }
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=RBM,PCM"
})
@AutoConfigureWebTestClient
class InterfaceSelectionAllIntegrationTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void keepsBothInterfacesEnabled() {
        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit")
                .bodyValue("{}")
                .exchange().expectStatus().is4xxClientError();
        webTestClient.post().uri("/v1/phones/111/agentMessages?agentId=a&messageId=b")
                .bodyValue("{}")
                .exchange().expectStatus().is4xxClientError();
    }
}


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=OSP"
})
@AutoConfigureWebTestClient
class InterfaceSelectionOspOnlyIntegrationTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void hidesRbmAndPcmEndpointsWhenOnlyOspEnabled() {
        webTestClient.post().uri("/v1/phones/111/agentMessages?agentId=a&messageId=b")
                .exchange().expectStatus().isNotFound();
        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void keepsOspEndpointsEnabled() {
        webTestClient.post().uri("/v3/auth/")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials")
                .exchange().expectStatus().isUnauthorized();
    }
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=RBM,PCM"
})
@AutoConfigureWebTestClient
class InterfaceSelectionWithoutOspIntegrationTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void hidesOspEndpointsWhenDisabled() {
        webTestClient.post().uri("/v3/auth/")
                .exchange().expectStatus().isNotFound();
        webTestClient.post().uri("/webhook/orange/bot-1/uuid-1")
                .bodyValue("{}")
                .exchange().expectStatus().isNotFound();
    }
}
