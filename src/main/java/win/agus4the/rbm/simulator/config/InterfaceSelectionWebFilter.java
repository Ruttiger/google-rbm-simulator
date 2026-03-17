package win.agus4the.rbm.simulator.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import win.agus4the.rbm.simulator.core.model.MaapChannel;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InterfaceSelectionWebFilter implements WebFilter {

    private final InterfaceActivationService interfaceActivationService;

    public InterfaceSelectionWebFilter(InterfaceActivationService interfaceActivationService) {
        this.interfaceActivationService = interfaceActivationService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if ((path.startsWith("/restadpt_generico1") || path.startsWith("/v1/provisioning/pcm"))
                && !interfaceActivationService.isEnabled(MaapChannel.PCM)) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().setComplete();
        }
        if ((path.startsWith("/v1") || path.startsWith("/token") || path.startsWith("/webhook/google"))
                && !path.startsWith("/v1/provisioning/pcm")
                && !interfaceActivationService.isEnabled(MaapChannel.RBM)) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
