package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.consumer.BrukerConsumer;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;

@Service
@RequiredArgsConstructor
public class BrukerService {
    private final BrukerConsumer brukerConsumer;

    public Mono<String> getId(String orgnummer, ServerWebExchange serverWebExchange) {
        return brukerConsumer.getBruker(orgnummer, serverWebExchange).map(BrukerDTO::id);
    }

    public Mono<String> getToken(String id, ServerWebExchange serverWebExchange) {
        return brukerConsumer.getToken(id, serverWebExchange);
    }
}
