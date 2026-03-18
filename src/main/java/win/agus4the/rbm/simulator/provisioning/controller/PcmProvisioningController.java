package win.agus4the.rbm.simulator.provisioning.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.agus4the.rbm.simulator.core.service.ProvisioningService;
import win.agus4the.rbm.simulator.provisioning.model.PcmWebhookProvisioningRequest;
import win.agus4the.rbm.simulator.provisioning.model.PcmWebhookProvisioningResponse;

@RestController
@RequestMapping("/v1/provisioning/pcm/webhooks")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency")
public class PcmProvisioningController {

    private final ProvisioningService provisioningService;

    public PcmProvisioningController(ProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @PutMapping("/{sender}")
    public ResponseEntity<PcmWebhookProvisioningResponse> upsert(
            @PathVariable String sender,
            @RequestBody PcmWebhookProvisioningRequest request
    ) {
        var saved = provisioningService.upsertPcm(sender, request.deliveryReportUrl(), request.smsDeliverUrl(), request.username(), request.password());
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/{sender}")
    public ResponseEntity<PcmWebhookProvisioningResponse> get(@PathVariable String sender) {
        return provisioningService.getPcm(sender)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sender}")
    public ResponseEntity<Void> delete(@PathVariable String sender) {
        provisioningService.deletePcm(sender);
        return ResponseEntity.noContent().build();
    }

    private PcmWebhookProvisioningResponse toResponse(win.agus4the.rbm.simulator.core.model.MaapProvisioningConfig config) {
        return new PcmWebhookProvisioningResponse(config.key(), config.deliveryReportUrl(), config.inboundMessageUrl(), config.username());
    }
}
