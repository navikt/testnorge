package no.nav.testnav.altinn3tilgangservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.AltinnConsumer;
import no.nav.testnav.altinn3tilgangservice.domain.PersonRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnBrukerTilgangService {

    private final AltinnConsumer altinnConsumer;

    public Mono<Boolean> harDollyTilgang(PersonRequest personRequest) {

        return altinnConsumer.getAuthorizedParties(personRequest.getIdent())
                .doOnNext(party -> log.info("AuthorizedParty {}", party))
                .filter(party -> party.getOrganizationNumber().equals(personRequest.getOrgnummer()))
                .filter(part -> part.getAuthorizedResources().contains("dolly"))
                .reduce(Boolean.FALSE, (a, b) -> Boolean.TRUE);
    }
}
