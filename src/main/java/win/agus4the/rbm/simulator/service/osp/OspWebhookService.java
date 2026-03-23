package win.agus4the.rbm.simulator.service.osp;

import org.springframework.stereotype.Service;
import win.agus4the.rbm.simulator.model.osp.OspWebhookAckResponse;

import java.util.Map;

@Service
public class OspWebhookService {

    public OspWebhookAckResponse receiveNotification(Map<String, Object> payload) {
        if (payload.containsKey("messageStatus")) {
            return new OspWebhookAckResponse("received", "messageStatus");
        }
        if (payload.containsKey("response")) {
            return new OspWebhookAckResponse("received", "response");
        }
        if (payload.containsKey("message")) {
            return new OspWebhookAckResponse("received", "message");
        }
        return new OspWebhookAckResponse("received", "unknown");
    }
}
