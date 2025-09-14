package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class UserController {

    private final BusinessMessagingService messagingService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(BusinessMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public record UsersRequest(List<String> users, String agentId) {
        public UsersRequest {
            users = List.copyOf(users);
        }
    }

    @PostMapping("/v1/users:batchGet")
    public Mono<Map<String, List<String>>> batchGet(@RequestBody UsersRequest request) {
        log.info("Batch get users agentId={} size={}", request.agentId(), request.users().size());
        return messagingService.reachableUsers(request.agentId(), request.users())
                .doOnNext(list -> log.debug("Reachable users agentId={} users={} ", request.agentId(), list))
                .map(list -> Map.of("reachableUsers", list));
    }
}
