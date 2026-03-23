package win.agus4the.rbm.simulator.controller.osp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.model.osp.OspWebhookAckResponse;
import win.agus4the.rbm.simulator.service.osp.OspWebhookService;

import java.util.Map;

@RestController
public class OspWebhookController {

    private final OspWebhookService ospWebhookService;

    public OspWebhookController(OspWebhookService ospWebhookService) {
        this.ospWebhookService = ospWebhookService;
    }

    @PostMapping(value = "/webhook/orange/{botId}/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OspWebhookAckResponse> receive(
            @PathVariable String botId,
            @PathVariable String uuid,
            @RequestBody Mono<Map<String, Object>> payload
    ) {
        return payload.map(ospWebhookService::receiveNotification);
    }
}
