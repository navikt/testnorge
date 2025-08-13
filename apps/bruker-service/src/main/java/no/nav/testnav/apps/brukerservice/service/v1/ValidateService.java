package no.nav.testnav.apps.brukerservice.service.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.consumer.AltinnTilgangServiceConsumer;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final AltinnTilgangServiceConsumer altinnTilgangServiceConsumer;

    public Mono<Void> validateOrganiasjonsnummerAccess(String organisasjonsnummer) {
        return altinnTilgangServiceConsumer
                .getOrganisasjon(organisasjonsnummer)
                .doOnNext(organisasjon -> {
                    if (!organisasjon.getOrganisasjonsnummer().equals(organisasjonsnummer)) {
                        throw new UserHasNoAccessToOrgnisasjonException(organisasjonsnummer);
                    }
                })
                .switchIfEmpty(Mono.error(new UserHasNoAccessToOrgnisasjonException(organisasjonsnummer)))
                .then();
    }
}
