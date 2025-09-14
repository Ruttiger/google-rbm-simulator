package win.agus4the.rbm.simulator.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.test.TestStrategy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class LogbookWebFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InMemoryHttpLogWriter logWriter;

    @Test
    void logsHttpExchange() {
        logWriter.reset();
        webTestClient.get().uri("/v1/regions").exchange().expectStatus().isOk();
        assertTrue(logWriter.getLogs().stream().anyMatch(l -> l.contains("/v1/regions")));
    }

    @TestConfiguration
    static class LogbookTestConfig {

        @Bean
        TestStrategy testStrategy() {
            return new TestStrategy();
        }

        @Bean
        InMemoryHttpLogWriter logWriter() {
            return new InMemoryHttpLogWriter();
        }

        @Bean
        Logbook logbook(HttpLogFormatter formatter, TestStrategy strategy, InMemoryHttpLogWriter writer) {
            return Logbook.builder()
                    .strategy(strategy)
                    .sink(new DefaultSink(formatter, writer))
                    .build();
        }
    }

    static class InMemoryHttpLogWriter implements HttpLogWriter {

        private final List<String> logs = new CopyOnWriteArrayList<>();

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void write(Precorrelation precorrelation, String request) throws IOException {
            logs.add(request);
        }

        @Override
        public void write(Correlation correlation, String response) throws IOException {
            logs.add(response);
        }

        List<String> getLogs() {
            return logs;
        }

        void reset() {
            logs.clear();
        }
    }
}

