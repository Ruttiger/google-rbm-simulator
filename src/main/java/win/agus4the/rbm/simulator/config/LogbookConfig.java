package win.agus4the.rbm.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;

/**
 * Configuration for Logbook HTTP logging. Registers a {@link Logbook} bean that
 * uses a {@link FunctionalHttpLogFormatter} so developers can tailor how
 * requests and responses are rendered in logs.
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
    public Logbook logbook(HttpLogFormatter formatter) {
        return Logbook.builder()
                .sink(new DefaultSink(formatter, new DefaultHttpLogWriter()))
                .build();
    }
}
