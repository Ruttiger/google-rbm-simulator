package win.agus4the.rbm.simulator.core.model;

public record MaapDispatchResult(
        String messageId,
        String deliveryReportUrl,
        boolean accepted
) {
}
