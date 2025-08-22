package com.messi.rbm.simulator.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "auth")
@Validated
/**
 * Configuration properties governing authentication behaviour of the simulator.
 */
public class AuthProperties {

    public enum Mode { PERMISSIVE, STRICT }

    @NotNull
    private Mode mode = Mode.PERMISSIVE;

    @NotBlank
    private String issuer;

    @NotBlank
    private String audience;

    private long tokenTtlSeconds = 3600;

    private boolean acceptAnyClientInPermissive = true;

    private List<Client> acceptedClients = new ArrayList<>();

    private List<String> allowedScopes = new ArrayList<>();

    @NotNull
    private Key key = new Key();

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public long getTokenTtlSeconds() {
        return tokenTtlSeconds;
    }

    public void setTokenTtlSeconds(long tokenTtlSeconds) {
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    public boolean isAcceptAnyClientInPermissive() {
        return acceptAnyClientInPermissive;
    }

    public void setAcceptAnyClientInPermissive(boolean acceptAnyClientInPermissive) {
        this.acceptAnyClientInPermissive = acceptAnyClientInPermissive;
    }

    public List<Client> getAcceptedClients() {
        return acceptedClients;
    }

    public void setAcceptedClients(List<Client> acceptedClients) {
        this.acceptedClients = acceptedClients;
    }

    public List<String> getAllowedScopes() {
        return allowedScopes;
    }

    public void setAllowedScopes(List<String> allowedScopes) {
        this.allowedScopes = allowedScopes;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public boolean isClientAccepted(String id, String secret) {
        if (mode == Mode.PERMISSIVE && acceptAnyClientInPermissive) {
            return true;
        }
        return acceptedClients.stream()
                .anyMatch(c -> c.getClientId().equals(id) && c.getClientSecret().equals(secret));
    }

    public List<String> filterScopes(List<String> requested) {
        if (requested == null || requested.isEmpty()) {
            return List.copyOf(allowedScopes);
        }
        return requested.stream().filter(allowedScopes::contains).toList();
    }

    public static class Client {
        private String clientId;
        private String clientSecret;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    public static class Key {
        private boolean generateOnStartup = true;
        private String keyId;
        private String privateKeyPem;
        private String publicKeyPem;

        public boolean isGenerateOnStartup() {
            return generateOnStartup;
        }

        public void setGenerateOnStartup(boolean generateOnStartup) {
            this.generateOnStartup = generateOnStartup;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getPrivateKeyPem() {
            return privateKeyPem;
        }

        public void setPrivateKeyPem(String privateKeyPem) {
            this.privateKeyPem = privateKeyPem;
        }

        public String getPublicKeyPem() {
            return publicKeyPem;
        }

        public void setPublicKeyPem(String publicKeyPem) {
            this.publicKeyPem = publicKeyPem;
        }
    }
}
