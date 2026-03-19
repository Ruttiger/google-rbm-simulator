package win.agus4the.rbm.simulator.controller.pcm;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.model.pcm.PCMMens;
import win.agus4the.rbm.simulator.model.pcm.PCMResponse;
import win.agus4the.rbm.simulator.service.pcm.PCMMessageService;

@RestController
public class PCMMensController {
    private final PCMMessageService pcmMessageService;

    public PCMMensController(PCMMessageService pcmMessageService) {
        this.pcmMessageService = pcmMessageService;
    }

    @PostMapping("/restadpt_generico1/smsTextSubmit")
    public Mono<ResponseEntity<PCMResponse>> smsTextSubmit(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @Valid @RequestBody Mono<PCMMens> body
    ) {
        return body.flatMap(req -> pcmMessageService.processRequest(authHeader, req, false));
    }

    @PostMapping("/restadpt_generico1/smsBinarySubmit")
    public Mono<ResponseEntity<PCMResponse>> smsBinarySubmit(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @Valid @RequestBody Mono<PCMMens> body
    ) {
        return body.flatMap(req -> pcmMessageService.processRequest(authHeader, req, true));
    }
}
