package no.nav.testnav.apps.organisasjontilgangservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.AltinnConsumer;
import no.nav.testnav.apps.organisasjontilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.apps.organisasjontilgangservice.database.repository.OrganisasjonTilgangRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
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
                                .flatMap(miljoe -> isTrue(miljoe) ?
                                        organisasjonTilgangRepository.findByOrganisasjonNummer(orgnummer) :
                                        Mono.just(OrganisasjonTilgang.builder()
                                                .organisasjonNummer(orgnummer)
                                                .miljoe("q1")
                                                .build()));
                    } else {
                        return Mono.error(new NotFoundException(
                                String.format("Organisasjonsnummer %s ble ikke funnet", orgnummer)));
                    }
                });
    }
}
