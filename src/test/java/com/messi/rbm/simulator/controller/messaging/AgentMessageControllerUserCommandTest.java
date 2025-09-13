package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.service.BusinessMessagingService;
import com.messi.rbm.simulator.service.WebhookDispatcherService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentMessageControllerUserCommandTest {

    @Mock
    private WebhookDispatcherService dispatcherService;

    @Mock
    private BusinessMessagingService messagingService;

    @InjectMocks
    private AgentMessageController controller;

    @Test
    void dispatchesUserEventWithType() {
        when(messagingService.saveAgentMessage(anyString(), anyString(), any()))
                .thenReturn(Mono.empty());
        when(dispatcherService.dispatchEvent(anyString(), any())).thenReturn(Mono.empty());

        Message message = new Message(null, null, new Message.AgentContentMessage("#USER:hello", null, null, null, null));

        controller.receiveMessage("12345", "AGENT", "1", message).block();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(dispatcherService).dispatchEvent(eq("AGENT"), captor.capture());
        Map<String, Object> payload = captor.getValue();
        assertThat(payload.get("eventType")).isEqualTo("USER_MESSAGE");
        assertThat(payload.get("text")).isEqualTo("hello");
    }
}

