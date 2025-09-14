package win.agus4the.rbm.simulator.service;

import win.agus4the.rbm.simulator.config.AuthProperties;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService createService(long ttlSeconds) throws Exception {
        AuthProperties props = new AuthProperties();
        props.setIssuer("issuer");
        props.setAudience("audience");
        props.setTokenTtlSeconds(ttlSeconds);
        AuthProperties.Key key = new AuthProperties.Key();
        key.setGenerateOnStartup(true);
        key.setKeyId("kid");
        props.setKey(key);
        JwtService service = new JwtService(props);
        service.init();
        return service;
    }

    @Test
    void generatesExpiredTokenWhenTtlNegative() throws Exception {
        JwtService service = createService(-60);
        String token = service.generateToken("subject", List.of("s1"));
        Date exp = SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime();
        assertTrue(exp.before(new Date()));
    }

    @Test
    void generatedTokenContainsScopesAndFutureExpiration() throws Exception {
        JwtService service = createService(60);
        String token = service.generateToken("sub", List.of("a","b"));
        var claims = SignedJWT.parse(token).getJWTClaimsSet();
        assertEquals("sub", claims.getSubject());
        assertEquals("a b", claims.getStringClaim("scope"));
        assertTrue(claims.getExpirationTime().after(new Date()));
    }
}
