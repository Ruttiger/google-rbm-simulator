package win.agus4the.rbm.simulator.service.osp;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OspTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Clock clock;
    private final Map<String, TokenRecord> tokens = new ConcurrentHashMap<>();

    public OspTokenService() {
        this(Clock.systemUTC());
    }

    OspTokenService(Clock clock) {
        this.clock = clock;
    }

    public String issueToken(String scope, long ttlSeconds) {
        purgeExpired();
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.put(token, new TokenRecord(scope, Instant.now(clock).plusSeconds(ttlSeconds)));
        return token;
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        TokenRecord record = tokens.get(token);
        if (record == null) {
            return false;
        }
        if (record.expiresAt().isAfter(Instant.now(clock))) {
            return true;
        }
        tokens.remove(token);
        return false;
    }

    private void purgeExpired() {
        Instant now = Instant.now(clock);
        tokens.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
    }

    private record TokenRecord(String scope, Instant expiresAt) {
    }
}
