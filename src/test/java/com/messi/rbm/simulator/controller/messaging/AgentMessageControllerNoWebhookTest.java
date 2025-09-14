package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.messaging.Message;
import com.messi.rbm.simulator.service.messaging.BusinessMessagingService;
import com.messi.rbm.simulator.service.communications.WebhookDispatcherService;
import com.messi.rbm.simulator.service.communications.WebhookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Ensures events are skipped when no webhook is configured for the agent.
 */
@ExtendWith(MockitoExtension.class)
class AgentMessageControllerNoWebhookTest {

    @Mock
    private WebhookDispatcherService dispatcherService;

    @Mock
    private BusinessMessagingService messagingService;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private AgentMessageController controller;

    @Test
    void skipsDispatchWhenNoWebhookPresent() {
        when(messagingService.saveAgentMessage(anyString(), anyString(), any()))
                .thenReturn(Mono.empty());
        when(webhookService.getConfig(anyString())).thenReturn(Mono.empty());

        Message message = new Message(null, null,
                new Message.AgentContentMessage("#READ #USER:hello", null, null, null, null));

        controller.receiveMessage("12345", "AGENT", "1", message).block();

        verify(dispatcherService, never()).dispatchEvent(anyString(), any());
    }
}
