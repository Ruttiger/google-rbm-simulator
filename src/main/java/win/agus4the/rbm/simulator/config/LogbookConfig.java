package win.agus4the.rbm.simulator.config;

import org.springframework.beans.factory.annotation.Value;
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

    private final boolean logBody;
    private final int maxBodySize;

    public LogbookConfig(
            @Value("${logbook.log-body:false}") boolean logBody,
            @Value("${logbook.max-body-size:2048}") int maxBodySize) {
        this.logBody = logBody;
        this.maxBodySize = maxBodySize;
    }

    @Bean
    public HttpLogFormatter httpLogFormatter() {
        return new FunctionalHttpLogFormatter(
                (precorrelation, request) -> {
                    StringBuilder log = new StringBuilder();
                    log.append("[IN] id=").append(precorrelation.getId())
                            .append(" method=").append(request.getMethod())
                            .append(" uri=").append(request.getRequestUri())
                            .append(" headers=").append(request.getHeaders());
                    if (logBody) {
                        try {
                            log.append(" body=").append(truncate(request.getBodyAsString()));
                        } catch (java.io.IOException ignored) {
                            log.append(" body=<error>");
                        }
                    }
                    return log.toString();
                },
                (correlation, response) -> {
                    StringBuilder log = new StringBuilder();
                    log.append("[OUT] id=").append(correlation.getId())
                            .append(" status=").append(response.getStatus())
                            .append(" headers=").append(response.getHeaders());
                    if (logBody) {
                        try {
                            log.append(" body=").append(truncate(response.getBodyAsString()));
                        } catch (java.io.IOException ignored) {
                            log.append(" body=<error>");
                        }
                    }
                    return log.toString();
                }
        );
    }

    private String truncate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= maxBodySize ? body : body.substring(0, maxBodySize);
    }

    @Bean
    public LogbookWebFilter logbookWebFilter(Logbook logbook) {
        return new LogbookWebFilter(logbook);
    }
}
