package win.agus4the.rbm.simulator.core.model;

public record MaapProvisioningConfig(
        MaapChannel channel,
        String key,
        String deliveryReportUrl,
        String inboundMessageUrl,
        String username,
        String password
) {
}
