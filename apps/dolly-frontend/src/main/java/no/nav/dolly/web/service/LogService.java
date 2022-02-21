package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.TilbakemeldingConsumer;
import no.nav.dolly.web.domain.Level;
import no.nav.dolly.web.domain.LogEvent;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final TilbakemeldingConsumer tilbakemeldingConsumer;

    public Mono<Void> log(LogEvent event, ServerWebExchange exchange) {
        logKibana(event);
        if (event.getMessage() != null && event.getLevel() == Level.INFO) {
            return tilbakemeldingConsumer.send(event.toTilbakemeldingDTO(), exchange);
        }
        return Mono.empty();
    }

    private void logKibana(LogEvent event) {
        var original = MDC.getCopyOfContextMap() == null ? new HashMap<String, String>() : MDC.getCopyOfContextMap();
        MDC.setContextMap(event.toPropertyMap());
        switch (event.getLevel()) {
            case TRACE -> log.trace(event.getMessage());
            case INFO -> log.info(event.getMessage());
            case WARNING -> log.warn(event.getMessage(), event);
            case ERROR -> log.error(event.getMessage(), event);
            default -> log.debug(event.getMessage());
        }
        MDC.setContextMap(original);
    }
}