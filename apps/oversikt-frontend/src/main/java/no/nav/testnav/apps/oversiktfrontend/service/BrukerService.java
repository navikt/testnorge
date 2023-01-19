package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.BrukerConsumer;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BrukerService {
    private final BrukerConsumer brukerConsumer;

    public Mono<String> getId(String orgnummer) {
        return brukerConsumer.getBruker(orgnummer).map(BrukerDTO::id);
    }

    public Mono<String> getToken(String id) {
        return brukerConsumer.getToken(id);
    }
}
