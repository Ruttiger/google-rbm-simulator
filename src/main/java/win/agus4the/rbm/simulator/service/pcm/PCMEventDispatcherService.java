package win.agus4the.rbm.simulator.service.pcm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.model.pcm.PCMEvent;
import win.agus4the.rbm.simulator.model.pcm.PCMMens;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PCMEventDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(PCMEventDispatcherService.class);

    private final WebClient webClient;
    private final String username;
    private final String password;

    public PCMEventDispatcherService(
            WebClient.Builder builder,
            @Value("${sim.pcm.username:pcm-user}") String username,
            @Value("${sim.pcm.password:pcm-pass}") String password
    ) {
        this.webClient = builder.build();
        this.username = username;
        this.password = password;
    }

    public void enqueueEvents(List<PCMEvent> events, PCMMens req, String messageId, String callbackUrl) {
        if (isMemoryHigh() || callbackUrl == null || callbackUrl.isBlank()) {
            return;
        }
        for (PCMEvent ev : events) {
            Mono.delay(Duration.ofMillis(ev.delayMs()))
                    .flatMap(t -> dispatchEvent(ev, req, messageId, callbackUrl))
                    .onErrorResume(e -> Mono.empty())
                    .subscribe();
        }
    }

    public Mono<Void> dispatchSingleDelivered(PCMMens req, String messageId, String callbackUrl) {
        PCMEvent event = new PCMEvent("DELIVERED", 0);
        return dispatchEvent(event, req, messageId, callbackUrl);
    }

    private Mono<Void> dispatchEvent(PCMEvent event, PCMMens req, String messageId, String callbackUrl) {
        Map<String, Object> dlrBody = buildEventBody(event, req, messageId);
        log.info("Dispatching PCM event={} to {}", event.type(), callbackUrl);
        return webClient.post()
                .uri(callbackUrl)
                .headers(h -> h.setBasicAuth(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dlrBody)
                .retrieve()
                .bodyToMono(Void.class)
                .then();
    }

    private boolean isMemoryHigh() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return (double) heap.getUsed() / heap.getMax() > 0.80;
    }

    private Map<String, Object> buildEventBody(PCMEvent event, PCMMens req, String messageId) {
        String recipient = req.recipients().get(0).to();
        return switch (event.type()) {
            case "DELIVERED" -> Map.of(
                    "sender", req.sender(),
                    "recipient", recipient,
                    "messageId", messageId,
                    "messageStatus", "Delivered",
                    "statusText", "external:DELIVRD,Date:" + LocalDateTime.now());
            case "REJECTED" -> Map.of(
                    "sender", req.sender(),
                    "recipient", recipient,
                    "messageId", messageId,
                    "messageStatus", "Rejected",
                    "statusText", "unknown");
            case "EXPIRED" -> Map.of(
                    "sender", req.sender(),
                    "recipient", recipient,
                    "messageId", messageId,
                    "messageStatus", "Expired",
                    "statusText", "expired:EXPIRED");
            default -> throw new IllegalStateException("PCM event not supported: " + event.type());
        };
    }
}
