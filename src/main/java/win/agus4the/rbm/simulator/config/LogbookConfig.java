package win.agus4the.rbm.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

/**
 * Configuration for Logbook HTTP logging. Registers a custom
 * {@link HttpLogFormatter} so developers can tailor how requests and responses
 * are rendered in logs while keeping the starter's auto-configuration and
 * property-based filters intact.
 */
@Configuration
public class LogbookConfig {

    @Bean
    public HttpLogFormatter httpLogFormatter() {
        return new FunctionalHttpLogFormatter(
                (precorrelation, request) -> request.getMethod() + " " + request.getRequestUri(),
                (correlation, response) -> "status=" + response.getStatus()
        );
    }

    @Bean
    public LogbookWebFilter logbookWebFilter(Logbook logbook) {
        return new LogbookWebFilter(logbook);
    }
}
