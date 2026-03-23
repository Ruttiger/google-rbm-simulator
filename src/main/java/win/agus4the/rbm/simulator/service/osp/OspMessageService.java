package win.agus4the.rbm.simulator.service.osp;

import org.springframework.stereotype.Service;
import win.agus4the.rbm.simulator.model.osp.OspMessageSendResponse;

import java.util.UUID;

@Service
public class OspMessageService {

    public OspMessageSendResponse sendMessage(String orangeChatbotId) {
        return new OspMessageSendResponse(UUID.randomUUID().toString(), orangeChatbotId, "accepted");
    }
}
