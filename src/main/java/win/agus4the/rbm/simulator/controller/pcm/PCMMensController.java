package win.agus4the.rbm.simulator.controller.pcm;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.model.pcm.PCMMens;
import win.agus4the.rbm.simulator.model.pcm.PCMResponse;
import win.agus4the.rbm.simulator.service.pcm.PCMMessageService;

/**
 * Receives user messages sent to the auth simulator.
 */
@RestController
public class PCMMensController {
    private final PCMMessageService pcmMessageService;

    public PCMMensController(PCMMessageService pcmMessageService) {
        this.pcmMessageService = pcmMessageService;
    }

    @PostMapping("/restadpt_generico1/smsTextSubmit")
    public Mono<ResponseEntity<PCMResponse>> receive(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @Valid @RequestBody Mono<PCMMens> body
    ) {

        return body.flatMap(req -> pcmMessageService.processRequest(authHeader, req));

    }
}
