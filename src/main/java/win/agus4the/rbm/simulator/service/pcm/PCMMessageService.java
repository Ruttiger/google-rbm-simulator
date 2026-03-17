package win.agus4the.rbm.simulator.service.pcm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.model.pcm.PCMEvent;
import win.agus4the.rbm.simulator.model.pcm.PCMMens;
import win.agus4the.rbm.simulator.model.pcm.PCMResponse;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class PCMMessageService {

    private static final Logger log = LoggerFactory.getLogger(PCMMessageService.class);

    private final PCMEventParser parser;
    private final PCMEventDispatcherService dispatcher;

    private final String validUser;
    private final String validPassword;
    private final WebClient webClient;
    public static final String DELIVERY_STATE_DELIVERED = "delivered";

    public PCMMessageService(
            @Value("${sim.pcm.username}") String validUser,
            @Value("${sim.pcm.password}") String validPassword,
            WebClient.Builder webClientBuilder,
            PCMEventParser parser,
            PCMEventDispatcherService dispatcher
    ) {
        this.validUser = validUser;
        this.validPassword = validPassword;
        this.parser = parser;
        this.dispatcher = dispatcher;
        this.webClient = webClientBuilder.build();
    }

    public Mono<ResponseEntity<PCMResponse>> processRequest(String authHeader, PCMMens req) {

        // --- Autenticación ---
        if (!isValidBasicAuth(authHeader)) {
            PCMResponse error = buildErrorResponse(
                    4001,
                    "Improper identification",
                    "Authentication failed or missing"
            );
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
        }

        // --- Procesamiento normal ---
        log.info("PCM message received sender={} recipients={} smsText={}",
                req.sender(),
                req.recipients() != null ? req.recipients().size() : 0,
                req.smsText()
        );

        String messageId = UUID.randomUUID().toString();

        if (shouldSendEvent(req.deliveryReport(), DELIVERY_STATE_DELIVERED)) {
            sendEventAsync(req.deliveryReportURL(), messageId, DELIVERY_STATE_DELIVERED);
        }


        // EXTRAER EVENTOS PCM DEL TEXTO
        List<PCMEvent> events = parser.parse(req.smsText());

        if (!events.isEmpty()) {
            dispatcher.enqueueEvents(events, req, messageId);
        }


        PCMResponse ok = new PCMResponse(1000, "Success",null, messageId);

        return Mono.just(ResponseEntity.ok(ok));
    }

    // ----------------AUTH---------------------

    private boolean isValidBasicAuth(String header) {
        if (header == null || !header.startsWith("Basic ")) return false;

        try {
            String base64 = header.substring(6);
            String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            if (parts.length != 2) return false;

            return parts[0].equals(validUser) && parts[1].equals(validPassword);

        } catch (Exception e) {
            return false;
        }
    }

    // ----------------Eventos ---------------------

    private boolean shouldSendEvent(String mode, String deliveryState) {
        if (mode == null) return false;

        return switch (mode) {
            case "All" -> true;
            case "None" -> false;
            case "Success" -> deliveryState.equals(DELIVERY_STATE_DELIVERED);
            case "Failure" -> !deliveryState.equals(DELIVERY_STATE_DELIVERED);
            default -> false;
        };
    }

    private void sendEventAsync(String url, String messageId, String state) {
        if (!StringUtils.hasText(url)) return;

        Map<String, Object> dlr = Map.of(
                "messageId", messageId,
                "status", state,
                "timestamp", System.currentTimeMillis()
        );

        webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dlr)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        ok -> log.info("Event sent to {}", url),
                        err -> log.warn("Event failed: {}", err.getMessage())
                );
    }

    // ----------------ERROR---------------------

    private PCMResponse buildErrorResponse(int statusCode, String text, String details) {
        return new PCMResponse(statusCode, text, details, null);
    }

}
