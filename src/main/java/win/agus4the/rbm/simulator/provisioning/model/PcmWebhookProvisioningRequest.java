package win.agus4the.rbm.simulator.provisioning.model;

public record PcmWebhookProvisioningRequest(
        String deliveryReportUrl,
        String smsDeliverUrl,
        String username,
        String password
) {
}
