package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class TesterController {

    private final BusinessMessagingService messagingService;

    public TesterController(BusinessMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/v1/phones/{msisdn}/testers")
    public Mono<Map<String, String>> addTester(@PathVariable String msisdn, @RequestParam String agentId) {
        return messagingService.addTester(agentId, msisdn);
    }
}
