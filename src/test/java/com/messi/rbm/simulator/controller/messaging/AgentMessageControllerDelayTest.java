package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.service.BusinessMessagingService;
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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentMessageControllerDelayTest {

    @Mock
    private WebhookService webhookService;

    @Mock
    private BusinessMessagingService messagingService;

    @InjectMocks
    private AgentMessageController controller;

    @Test
    void schedulesDeliveredEventWithDelay() {
        when(messagingService.saveAgentMessage(anyString(), anyString(), any()))
                .thenReturn(Mono.empty());
        when(webhookService.sendCallback(anyString(), any()))
                .thenReturn(Mono.empty());

        Message message = new Message(
                null,
                null,
                new Message.AgentContentMessage("#DELIVERED(delay=100)", null, null, null, null)
        );

        controller.receiveMessage("12345", "AGENT", "1", message).block();

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(webhookService, timeout(300).times(1))
                .sendCallback(eq("AGENT"), captor.capture());

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) captor.getValue();
        assertThat(payload.get("event")).isEqualTo("DELIVERED");
        assertThat(payload.get("msisdn")).isEqualTo("12345");
    }
}
