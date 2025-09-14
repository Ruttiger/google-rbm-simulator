package win.agus4the.rbm.simulator.auth;

import win.agus4the.rbm.simulator.config.AuthProperties;
import win.agus4the.rbm.simulator.service.JwtService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Simple WebFilter validating JWT tokens for /v1 requests.
 */
@Component
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class JwtAuthFilter implements WebFilter {

    private final JwtService jwtService;
    private final AuthProperties authProperties;

    public JwtAuthFilter(JwtService jwtService, AuthProperties authProperties) {
        this.jwtService = jwtService;
        this.authProperties = authProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!path.startsWith("/v1")) {
            return chain.filter(exchange);
        }
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            AuthProperties.Mode mode = authProperties.getMode();
            if (mode == null || mode == AuthProperties.Mode.PERMISSIVE) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = auth.substring(7);
        if (!jwtService.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
