package win.agus4the.rbm.simulator.service.pcm;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.config.MaapSimulatorProperties;
import win.agus4the.rbm.simulator.core.model.MaapDispatchResult;
import win.agus4the.rbm.simulator.core.service.ProvisioningService;
import win.agus4the.rbm.simulator.model.pcm.PCMEvent;
import win.agus4the.rbm.simulator.model.pcm.PCMMens;
import win.agus4the.rbm.simulator.model.pcm.PCMResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependencies")
public class PCMMessageService {

    private static final Logger log = LoggerFactory.getLogger(PCMMessageService.class);
    public static final String DELIVERY_STATE_DELIVERED = "delivered";

    private final PCMEventParser parser;
    private final PCMEventDispatcherService dispatcher;
    private final ProvisioningService provisioningService;
    private final MaapSimulatorProperties maapSimulatorProperties;

    private final String validUser;
    private final String validPassword;

    public PCMMessageService(
            @Value("${sim.pcm.username:pcm-user}") String validUser,
            @Value("${sim.pcm.password:pcm-pass}") String validPassword,
            PCMEventParser parser,
            PCMEventDispatcherService dispatcher,
            ProvisioningService provisioningService,
            MaapSimulatorProperties maapSimulatorProperties
    ) {
        this.validUser = validUser;
        this.validPassword = validPassword;
        this.parser = parser;
        this.dispatcher = dispatcher;
        this.provisioningService = provisioningService;
        this.maapSimulatorProperties = maapSimulatorProperties;
    }

    public Mono<ResponseEntity<PCMResponse>> processRequest(String authHeader, PCMMens req, boolean binarySubmit) {
        if (!isValidBasicAuth(authHeader)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(buildErrorResponse(4001, "Improper identification", "Authentication failed or missing")));
        }

        if (!req.hasPayload()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(buildErrorResponse(4004, "Validation error", "smsText or smsBinary is required")));
        }

        if (binarySubmit && (req.smsBinary() == null || req.smsBinary().isBlank())) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(buildErrorResponse(4004, "Validation error", "smsBinary is required for smsBinarySubmit")));
        }

        String callbackUrl = resolveDeliveryReportUrl(req);
        if (callbackUrl == null && maapSimulatorProperties.isStrictPcmDeliveryReportRouting()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(buildErrorResponse(2000, "Client error", "deliveryReportURL unavailable and strict mode enabled")));
        }

        String messageId = UUID.randomUUID().toString();
        log.info("PCM message received sender={} recipients={} callbackConfigured={}",
                req.sender(), req.recipients().size(), callbackUrl != null);

        List<PCMEvent> events = req.smsText() == null ? List.of() : parser.parse(req.smsText());
        if (!events.isEmpty()) {
            dispatcher.enqueueEvents(events, req, messageId, callbackUrl);
        }

        if (shouldSendEvent(req.deliveryReport(), DELIVERY_STATE_DELIVERED) && callbackUrl != null) {
            dispatcher.dispatchSingleDelivered(req, messageId, callbackUrl)
                    .onErrorResume(e -> Mono.empty())
                    .subscribe();
        }

        MaapDispatchResult dispatchResult = new MaapDispatchResult(messageId, callbackUrl, true);
        return Mono.just(ResponseEntity.ok(new PCMResponse(1000, "Success", null, dispatchResult.messageId())));
    }

    private String resolveDeliveryReportUrl(PCMMens req) {
        if (req.deliveryReportURL() != null && !req.deliveryReportURL().isBlank()) {
            return req.deliveryReportURL();
        }
        return provisioningService.getPcm(req.sender()).map(config -> config.deliveryReportUrl()).orElse(null);
    }

    private boolean isValidBasicAuth(String header) {
        if (header == null || !header.startsWith("Basic ")) return false;

        try {
            String decoded = new String(Base64.getDecoder().decode(header.substring(6)), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            return parts.length == 2 && parts[0].equals(validUser) && parts[1].equals(validPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

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

    private PCMResponse buildErrorResponse(int statusCode, String text, String details) {
        return new PCMResponse(statusCode, text, details, null);
    }
}
