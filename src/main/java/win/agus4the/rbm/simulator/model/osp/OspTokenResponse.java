package win.agus4the.rbm.simulator.model.osp;

public record OspTokenResponse(
        String access_token,
        String token_type,
        long expires_in,
        String scope
) {
}
