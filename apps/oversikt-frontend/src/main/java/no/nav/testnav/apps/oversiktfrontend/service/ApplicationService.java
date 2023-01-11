package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.AppTilgangAnalyseConsumer;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final AppTilgangAnalyseConsumer consumer;

    public Flux<Application> getApplications(ServerWebExchange exchange) {
        return consumer.getApplications(exchange);
    }

}
