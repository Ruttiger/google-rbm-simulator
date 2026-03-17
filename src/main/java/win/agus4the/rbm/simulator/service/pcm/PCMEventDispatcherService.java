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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class PCMEventDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(PCMEventDispatcherService.class);

    private final WebClient webClient;
    private final String username;
    private final String password;

    public static final String SENDER = "sender";
    public static final String RECIPIENT = "recipient";
    public static final String MESSAGE_STATUS = "messageStatus";
    public static final String STATUS_TEXT = "statusText";
    public static final String MESSAGE_ID = "messageId";

    private final Queue<PCMEvent> eventQueue = new ConcurrentLinkedQueue<>();

    public PCMEventDispatcherService(
            WebClient.Builder builder,
            @Value("${sim.pcm.username}") String username,
            @Value("${sim.pcm.password}") String password
    ) {
        this.webClient = builder.build();
        this.username = username;
        this.password = password;
    }

    /**
     * Encola los eventos extraídos del smsText y los envía tras su delay.
     */
    public void enqueueEvents(List<PCMEvent> events, PCMMens req, String messageId) {

        if (isMemoryHigh()) {
            log.warn("Heap > 80%. PCM events discarded.");
            return;
        }

        for (PCMEvent ev : events) {
            eventQueue.add(ev);

            Mono.delay(Duration.ofMillis(ev.delayMs()))
                    .flatMap(t -> dispatchEvent(ev, req, messageId))
                    .onErrorResume(e -> {
                        log.warn("Error dispatching PCM event={}: {}", ev.type(), e.getMessage());
                        return Mono.empty();
                    })
                    .subscribe();
        }
    }

    /**
     * Envia un único evento PCM al webhook (deliveryReportURL)
     */
    private Mono<Void> dispatchEvent(PCMEvent event, PCMMens req, String messageId) {

        String callbackUrl = req.deliveryReportURL();
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
        double ratio = (double) heap.getUsed() / heap.getMax();
        return ratio > 0.80;
    }

    /**
     * Construye el cuerpo del evento igual que los mensajes reales de PCM.
     */
    private Map<String, Object> buildEventBody(PCMEvent event, PCMMens req, String messageId) {

        return switch (event.type()) {

            case "DELIVERED" -> Map.of(
                    SENDER, req.sender(),
                    RECIPIENT, req.recipients().get(0),
                    MESSAGE_ID, messageId,
                    MESSAGE_STATUS, "Delivered",
                    STATUS_TEXT, "external:DELIVRD,Date:" + LocalDateTime.now()
            );

            case "REJECTED" -> Map.of(
                    SENDER, req.sender(),
                    RECIPIENT, req.recipients().get(0),
                    MESSAGE_ID, messageId,
                    MESSAGE_STATUS, "Rejected",
                    STATUS_TEXT, "unknown"
            );

            case "EXPIRED" -> Map.of(
                    SENDER, req.sender(),
                    RECIPIENT, req.recipients().get(0),
                    MESSAGE_ID, messageId,
                    MESSAGE_STATUS, "Expired",
                    STATUS_TEXT, "expired:EXPIRED"
            );

            default -> throw new IllegalStateException("PCM event not supported: " + event.type());
        };
    }
}