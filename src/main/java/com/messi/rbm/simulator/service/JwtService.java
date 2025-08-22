package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.config.AuthProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Service responsible for generating JWT tokens used by the simulator.
 */
@Service
public class JwtService {

    private final AuthProperties properties;
    private RSASSASigner signer;
    private RSAKey rsaKey;

    /**
     * Creates a JWT service with the given configuration properties.
     *
     * @param properties authentication configuration.
     */
    public JwtService(final AuthProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() throws Exception {
        AuthProperties.Key keyProps = properties.getKey();
        if (keyProps.isGenerateOnStartup()) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                    .privateKey((RSAPrivateKey) kp.getPrivate())
                    .keyID(keyProps.getKeyId())
                    .build();
        } else {
            RSAPrivateKey priv = loadPrivateKey(keyProps.getPrivateKeyPem());
            RSAPublicKey pub = loadPublicKey(keyProps.getPublicKeyPem());
            rsaKey = new RSAKey.Builder(pub).privateKey(priv).keyID(keyProps.getKeyId()).build();
        }
        signer = new RSASSASigner(rsaKey);
    }

    private RSAPrivateKey loadPrivateKey(final String pem) throws Exception {
        String content = pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----(\\r?\\n)?", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(content);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private RSAPublicKey loadPublicKey(final String pem) throws Exception {
        String content = pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----(\\r?\\n)?", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(content);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(decoded));
    }

    public String generateToken(final String subject, final List<String> scopes) throws JOSEException {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(properties.getIssuer())
                .audience(properties.getAudience())
                .subject(subject)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(properties.getTokenTtlSeconds())))
                .claim("scope", String.join(" ", scopes))
                .build();
        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                claims
        );
        jwt.sign(signer);
        return jwt.serialize();
    }

    public RSAKey getRsaKey() {
        return rsaKey.toPublicJWK();
    }
}
