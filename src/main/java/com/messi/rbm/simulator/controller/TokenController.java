package com.messi.rbm.simulator.controller;

import com.messi.rbm.simulator.config.AuthProperties;
import com.messi.rbm.simulator.service.JwtService;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Issues OAuth tokens for clients interacting with the simulator.
 */
@RestController
public class TokenController {

    private final AuthProperties properties;
    private final JwtService jwtService;

    public TokenController(AuthProperties properties, JwtService jwtService) {
        this.properties = properties;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> token(
            @RequestHeader(value = "Authorization", required = false) String auth,
            ServerWebExchange exchange) {
        return exchange.getFormData().flatMap(form -> {
            String grantType = form.getFirst("grant_type");
            if ("client_credentials".equals(grantType)) {
                ClientCredentials creds = extractClient(auth, form);
                if (creds == null || !properties.isClientAccepted(creds.clientId(), creds.clientSecret())) {
                    return Mono.just(ResponseEntity.status(401).build());
                }
                List<String> scopes = properties.filterScopes(parseScope(form.getFirst("scope")));
                return generateResponse(creds.clientId(), scopes);
            } else if ("urn:ietf:params:oauth:grant-type:jwt-bearer".equals(grantType)) {
                String assertion = form.getFirst("assertion");
                String subject = parseSubject(assertion);
                List<String> scopes = properties.filterScopes(parseScope(form.getFirst("scope")));
                return generateResponse(subject, scopes);
            } else {
                return Mono.just(ResponseEntity.badRequest().build());
            }
        });
    }

    private Mono<ResponseEntity<Map<String, Object>>> generateResponse(String subject, List<String> scopes) {
        try {
            String token = jwtService.generateToken(subject, scopes);
            Map<String, Object> resp = Map.of(
                    "access_token", token,
                    "token_type", "Bearer",
                    "expires_in", properties.getTokenTtlSeconds(),
                    "scope", String.join(" ", scopes)
            );
            return Mono.just(ResponseEntity.ok(resp));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private List<String> parseScope(String scope) {
        if (scope == null || scope.isEmpty()) {
            return List.of();
        }
        return List.of(scope.split(" "));
    }

    private ClientCredentials extractClient(String auth, MultiValueMap<String, String> form) {
        if (auth != null && auth.startsWith("Basic ")) {
            try {
                byte[] decoded = Base64.getDecoder().decode(auth.substring(6));
                String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                String[] parts = decodedStr.split(":", 2);
                return new ClientCredentials(parts[0], parts.length > 1 ? parts[1] : "");
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return new ClientCredentials(form.getFirst("client_id"), form.getFirst("client_secret"));
    }

    private String parseSubject(String assertion) {
        if (assertion == null) {
            return "anonymous";
        }
        try {
            return SignedJWT.parse(assertion).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            return "anonymous";
        }
    }

    private record ClientCredentials(String clientId, String clientSecret) {
    }
}
