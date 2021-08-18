package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.dolly.web.consumers.TilbakemeldingConsumer;
import no.nav.dolly.web.domain.Level;
import no.nav.dolly.web.domain.LogEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final TilbakemeldingConsumer tilbakemeldingConsumer;

    private void logKibana(LogEvent event) {
        var original = MDC.getCopyOfContextMap();
        MDC.setContextMap(event.toPropertyMap());
        switch (event.getLevel()) {
            case TRACE:
                log.trace(event.getMessage());
                break;
            case INFO:
                log.info(event.getMessage());
                break;
            case WARNING:
                log.warn(event.getMessage());
                break;
            case ERROR:
                log.error(event.getMessage());
                break;
            default:
                log.debug(event.getMessage());
                break;
        }
        MDC.setContextMap(original);
    }

    public Mono<Void> log(LogEvent event) {
        logKibana(event);
        if (event.getMessage() != null && event.getLevel() == Level.INFO) {
            return tilbakemeldingConsumer.send(event.toTilbakemeldingDTO());
        }
        return Mono.empty();
    }
}