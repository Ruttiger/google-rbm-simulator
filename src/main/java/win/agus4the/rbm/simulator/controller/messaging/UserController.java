package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@RestController
@SuppressFBWarnings(
        value = {"EI_EXPOSE_REP2", "CT_CONSTRUCTOR_THROW"},
        justification = "Dependencies are injected and constructor wiring is framework-managed")
public class UserController {

    private final BusinessMessagingService messagingService;
    private final Supplier<Random> randomSupplier;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(BusinessMessagingService messagingService,
                          ObjectProvider<Supplier<Random>> randomSupplierProvider) {
        this.messagingService = messagingService;
        this.randomSupplier = randomSupplierProvider.getIfAvailable(() -> ThreadLocalRandom::current);
    }

    public record UsersRequest(List<String> users, String agentId) {
        public UsersRequest {
            users = users == null ? List.of() : List.copyOf(users);
        }
    }

    @PostMapping("/v1/users:batchGet")
    public Mono<ResponseEntity<Map<String, Object>>> batchGet(@RequestBody UsersRequest request) {
        final String agentId = request.agentId();
        final List<String> users = request.users();
        log.info("Batch get users agentId={} size={}", agentId, users.size());

        if (agentId == null || agentId.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "agentId requerido")));
        }

        final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{1,14}$");
        if (users.stream().anyMatch(n -> n == null || !E164.matcher(n).matches())) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Todos los MSISDN deben estar en formato E.164")));
        }

        final int n = users.size();
        if (n < 500 || n > 10_000) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar entre 500 y 10000 números únicos")));
        }

        if (n != new HashSet<>(users).size()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Los números deben ser únicos")));
        }

        return messagingService.reachableUsers(agentId, users)
                .map(reachable -> {
                    int sampleSize = (int) Math.ceil(n * 0.75);
                    List<String> sample = new ArrayList<>(users);
                    Collections.shuffle(sample, randomSupplier.get());
                    sample = sample.subList(0, sampleSize);

                    long reachableInSample = sample.stream().filter(reachable::contains).count();

                    Map<String, Object> body = new HashMap<>();
                    body.put("reachableUsers", reachable);
                    body.put("totalRandomSampleUserCount", sampleSize);
                    body.put("reachableRandomSampleUserCount", reachableInSample);
                    return ResponseEntity.ok(body);
                });
    }
}
