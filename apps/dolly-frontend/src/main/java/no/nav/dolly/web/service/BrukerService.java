package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.web.consumers.BrukerConsumer;
import no.nav.dolly.web.consumers.dto.BrukerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BrukerService {
    private final BrukerConsumer brukerConsumer;

    public Mono<String> getId(String orgnummer, ServerWebExchange serverWebExchange) {
        return brukerConsumer.getBruker(orgnummer, serverWebExchange).map(BrukerDTO::id);
    }
}
