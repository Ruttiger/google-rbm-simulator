package win.agus4the.rbm.simulator.config;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * {@link HttpLogFormatter} implementation delegating formatting of requests and
 * responses to provided functions. This allows applications to customize how
 * HTTP interactions are logged.
 */
public final class FunctionalHttpLogFormatter implements HttpLogFormatter {

    private final BiFunction<Precorrelation, HttpRequest, String> requestFormatter;
    private final BiFunction<Correlation, HttpResponse, String> responseFormatter;

    public FunctionalHttpLogFormatter(
            BiFunction<Precorrelation, HttpRequest, String> requestFormatter,
            BiFunction<Correlation, HttpResponse, String> responseFormatter) {
        this.requestFormatter = Objects.requireNonNull(requestFormatter);
        this.responseFormatter = Objects.requireNonNull(responseFormatter);
    }

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        return requestFormatter.apply(precorrelation, request);
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
        return responseFormatter.apply(correlation, response);
    }
}
