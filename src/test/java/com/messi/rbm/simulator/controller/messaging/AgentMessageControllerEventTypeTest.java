package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.model.WebhookConfig;
import com.messi.rbm.simulator.service.BusinessMessagingService;
import com.messi.rbm.simulator.service.WebhookDispatcherService;
import com.messi.rbm.simulator.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentMessageControllerEventTypeTest {

    @Mock
    private WebhookDispatcherService dispatcherService;

    @Mock
    private BusinessMessagingService messagingService;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private AgentMessageController controller;

    @Test
    void dispatchesRevokedEvent() {
        when(messagingService.saveAgentMessage(anyString(), anyString(), any()))
                .thenReturn(Mono.empty());
        when(dispatcherService.dispatchEvent(anyString(), any()))
                .thenReturn(Mono.empty());
        when(webhookService.getConfig(anyString()))
                .thenReturn(Mono.just(new WebhookConfig("http://example", null)));

        Message message = new Message(
                null,
                null,
                new Message.AgentContentMessage("#REVOKED", null, null, null, null)
        );

        controller.receiveMessage("12345", "AGENT", "1", message).block();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(dispatcherService).dispatchEvent(eq("AGENT"), captor.capture());
        assertThat(captor.getValue().get("eventType")).isEqualTo("REVOKED");
    }

    @Test
    void ignoresUnsupportedEvent() {
        when(messagingService.saveAgentMessage(anyString(), anyString(), any()))
                .thenReturn(Mono.empty());
        when(webhookService.getConfig(anyString()))
                .thenReturn(Mono.just(new WebhookConfig("http://example", null)));

        Message message = new Message(
                null,
                null,
                new Message.AgentContentMessage("#DISPLAYED", null, null, null, null)
        );

        controller.receiveMessage("12345", "AGENT", "1", message).block();

        verify(dispatcherService, never()).dispatchEvent(anyString(), any());
    }
}

