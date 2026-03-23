package win.agus4the.rbm.simulator.controller.osp;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.config.OspProperties;
import win.agus4the.rbm.simulator.model.osp.OspTokenResponse;
import win.agus4the.rbm.simulator.service.osp.OspTokenService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class OspAuthController {

    private final String clientId;
    private final String clientSecret;
    private final long tokenTtlSeconds;
    private final String defaultScope;
    private final OspTokenService ospTokenService;

    public OspAuthController(OspProperties ospProperties, OspTokenService ospTokenService) {
        this.clientId = ospProperties.getClientId();
        this.clientSecret = ospProperties.getClientSecret();
        this.tokenTtlSeconds = ospProperties.getTokenTtlSeconds();
        this.defaultScope = ospProperties.getDefaultScope();
        this.ospTokenService = ospTokenService;
    }

    @PostMapping(value = "/v3/auth/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<OspTokenResponse>> token(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            ServerWebExchange exchange
    ) {
        return exchange.getFormData().map(formData -> {
            if (!"client_credentials".equals(formData.getFirst("grant_type")) || !isBasicAuthValid(authorization)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String scope = resolveScope(formData.getFirst("scope"));
            String token = ospTokenService.issueToken(scope, tokenTtlSeconds);
            OspTokenResponse response = new OspTokenResponse(
                    token,
                    "Bearer",
                    tokenTtlSeconds,
                    scope
            );
            return ResponseEntity.ok(response);
        });
    }

    private boolean isBasicAuthValid(String authorization) {
        if (authorization == null || !authorization.startsWith("Basic ")) {
            return false;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(authorization.substring(6)), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            if (parts.length != 2) {
                return false;
            }
            return clientId.equals(parts[0]) && clientSecret.equals(parts[1]);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private String resolveScope(String scope) {
        return (scope == null || scope.isBlank()) ? defaultScope : scope;
    }
}
