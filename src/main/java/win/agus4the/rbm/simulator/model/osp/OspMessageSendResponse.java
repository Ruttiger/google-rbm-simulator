package win.agus4the.rbm.simulator.model.osp;

public record OspMessageSendResponse(
        String messageId,
        String orangeChatbotId,
        String status
) {
}
