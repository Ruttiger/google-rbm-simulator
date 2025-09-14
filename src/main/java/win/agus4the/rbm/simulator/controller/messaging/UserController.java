package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
        return messagingService.reachableUsers(request.agentId(), request.users())
                .map(list -> Map.of("reachableUsers", list));
    }
}
