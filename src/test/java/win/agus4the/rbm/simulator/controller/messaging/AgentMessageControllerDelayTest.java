package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.model.messaging.Message;
import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;
import win.agus4the.rbm.simulator.service.communications.WebhookDispatcherService;
import win.agus4the.rbm.simulator.service.communications.WebhookService;
import win.agus4the.rbm.simulator.model.communications.WebhookConfig;
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
    private WebhookDispatcherService dispatcherService;

    @Mock
    private BusinessMessagingService messagingService;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private AgentMessageController controller;

    @Test
    void schedulesDeliveredEventWithDelay() {
        when(messagingService.saveAgentMessage(anyString(), anyString(), any()))
                .thenReturn(Mono.empty());
        when(dispatcherService.dispatchEvent(anyString(), any()))
                .thenReturn(Mono.empty());
        when(webhookService.getConfig(anyString())).thenReturn(Mono.just(new WebhookConfig("http://example", null)));

        Message message = new Message(
                null,
                null,
                new Message.AgentContentMessage("#DELIVERED(delay=100)", null, null, null, null)
        );

        controller.receiveMessage("12345", "AGENT", "1", message).block();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(dispatcherService, timeout(300).times(1))
                .dispatchEvent(eq("AGENT"), captor.capture());

        Map<String, Object> payload = captor.getValue();
        assertThat(payload.get("eventType")).isEqualTo("DELIVERED");
        assertThat(payload.get("senderPhoneNumber")).isEqualTo("12345");
        assertThat(payload.get("messageId")).isEqualTo("1");
    }
}
