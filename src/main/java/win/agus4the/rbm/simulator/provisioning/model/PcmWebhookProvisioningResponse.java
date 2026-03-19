package win.agus4the.rbm.simulator.provisioning.model;

public record PcmWebhookProvisioningResponse(
        String sender,
        String deliveryReportUrl,
        String smsDeliverUrl,
        String username
) {
}
