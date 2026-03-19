package win.agus4the.rbm.simulator.model.pcm;


public record PCMResponse(
        int statusCode,
        String statusText,
        String details,
        String messageId
) {

}
