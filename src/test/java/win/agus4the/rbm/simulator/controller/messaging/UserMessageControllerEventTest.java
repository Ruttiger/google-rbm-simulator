package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.model.messaging.Message;
import win.agus4the.rbm.simulator.service.communications.WebhookDispatcherService;
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
class UserMessageControllerEventTest {

    @Mock
    private WebhookDispatcherService dispatcherService;

    @InjectMocks
    private UserMessageController controller;

    @Test
    void dispatchesUserMessageEventType() {
        when(dispatcherService.dispatchEvent(anyString(), any())).thenReturn(Mono.empty());

        Message message = new Message(null, null, new Message.AgentContentMessage("hi", null, null, null, null));

        controller.userMessage("123", "AGENT", message).block();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(dispatcherService).dispatchEvent(eq("AGENT"), captor.capture());

        Map<String, Object> payload = captor.getValue();
        assertThat(payload.get("eventType")).isEqualTo("USER_MESSAGE");
        assertThat(payload.get("text")).isEqualTo("hi");
    }
}

