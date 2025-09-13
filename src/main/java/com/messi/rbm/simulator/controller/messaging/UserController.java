package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final BusinessMessagingService messagingService;

    public UserController(BusinessMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public record UsersRequest(List<String> users, String agentId) { }

    @PostMapping("/v1/users:batchGet")
    public Mono<Map<String, List<String>>> batchGet(@RequestBody UsersRequest request) {
        return messagingService.reachableUsers(request.agentId(), request.users())
                .map(list -> Map.of("reachableUsers", list));
    }
}
