package no.nav.testnav.apps.brukerservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.consumer.PersonOrganisasjonTilgangConsumer;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final PersonOrganisasjonTilgangConsumer client;

    public Mono<Void> validateOrganiasjonsnummerAccess(String organisasjonsnummer) {
        return client
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
