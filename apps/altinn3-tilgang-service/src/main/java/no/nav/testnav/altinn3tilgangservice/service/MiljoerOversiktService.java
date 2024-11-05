package no.nav.testnav.altinn3tilgangservice.service;

import javassist.NotFoundException;
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
