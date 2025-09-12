package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings("ALL")
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
