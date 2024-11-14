package no.nav.testnav.altinn3tilgangservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.AltinnConsumer;
import no.nav.testnav.altinn3tilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.altinn3tilgangservice.database.repository.OrganisasjonTilgangRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class MiljoerOversiktService {

    private final AltinnConsumer altinnConsumer;
    private final OrganisasjonTilgangRepository organisasjonTilgangRepository;

    public Mono<OrganisasjonTilgang> getMiljoe(String orgnummer) {

        return altinnConsumer.getOrganisasjoner()
                .collectList()
                .flatMap(bedrifter -> {

                    if (bedrifter.stream().anyMatch(bedrift ->
                            orgnummer.equals(bedrift.getOrganisasjonsnummer()))) {

                        return organisasjonTilgangRepository.existsByOrganisasjonNummer(orgnummer)
                                .flatMap(exists -> isTrue(exists) ?
                                        organisasjonTilgangRepository.findByOrganisasjonNummer(orgnummer) :
                                        Mono.just(OrganisasjonTilgang.builder()
                                                .organisasjonNummer(orgnummer)
                                                .miljoe("q1")
                                                .build()));
                    } else {
                        return throwError(orgnummer);
                    }
                });
    }

    public Mono<OrganisasjonTilgang> updateMiljoe(String orgnummer, String miljoe) {

        return organisasjonTilgangRepository.existsByOrganisasjonNummer(orgnummer)
                .flatMap(exists -> isTrue(exists) ?
                        organisasjonTilgangRepository.findByOrganisasjonNummer(orgnummer)
                                .flatMap(organisasjon -> {
                                    organisasjon.setMiljoe(miljoe);
                                    return organisasjonTilgangRepository.save(organisasjon);
                                }) :
                        organisasjonTilgangRepository.save(OrganisasjonTilgang.builder()
                                .organisasjonNummer(orgnummer)
                                .miljoe(miljoe)
                                .build()));
    }

    private static Mono<OrganisasjonTilgang> throwError(String orgnummer) {

        return Mono.just(OrganisasjonTilgang.builder()
                .organisasjonNummer(orgnummer)
                .feilmelding("404 Not found: Organisasjonsnummer %s ble ikke funnet".formatted(orgnummer))
                .build());
    }
}
