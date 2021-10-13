package no.nav.testnav.apps.brukerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.brukerservice.client.PersonOrganisasjonTilgangClient;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final PersonOrganisasjonTilgangClient client;
    public Mono<Void> validateOrganiasjonsnummerAccess(String organisasjonsnummer) {
        return client
                .getOrganisastion(organisasjonsnummer)
                .switchIfEmpty(Mono.error(new UserHasNoAccessToOrgnisasjonException(organisasjonsnummer)))
                .then();
    }
}
