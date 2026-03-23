package win.agus4the.rbm.simulator.controller.osp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.model.osp.OspMessageSendResponse;
import win.agus4the.rbm.simulator.service.osp.OspMessageService;

import java.util.Map;

@RestController
public class OspBotMessageController {

    private final OspMessageService ospMessageService;

    public OspBotMessageController(OspMessageService ospMessageService) {
        this.ospMessageService = ospMessageService;
    }

    @PostMapping(value = "/v3/bot/v1/{orangeChatbotId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OspMessageSendResponse> sendMessage(
            @PathVariable String orangeChatbotId,
            @RequestBody Mono<Map<String, Object>> payload
    ) {
        return payload.map(ignored -> ospMessageService.sendMessage(orangeChatbotId));
    }
}
