package no.nav.testnav.altinn3tilgangservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.AltinnConsumer;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AuthorizedPartyDTO;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnBrukerTilgangService {

    private static final String DOLLY_RESOURCE = "nav_dolly_tilgang-samarbeidspartnere";
    private final AltinnConsumer altinnConsumer;

    public Flux<OrganisasjonDTO> getPersonOrganisasjonTilgang(String ident) {

        return altinnConsumer.getAuthorizedParties(ident)
                .flatMap(authorizedParty -> getUnitsAndSubunits(new ArrayList<>(), authorizedParty))
                .flatMap(Flux::fromIterable);
    }

    private Mono<List<OrganisasjonDTO>> getUnitsAndSubunits(List<OrganisasjonDTO> organisasjoner,
                                                            AuthorizedPartyDTO authorizedParties) {

        organisasjoner.addAll(Stream.of(authorizedParties)
                .filter(part -> part.getAuthorizedResources().contains(DOLLY_RESOURCE))
                .map(part -> OrganisasjonDTO.builder()
                        .navn(part.getName())
                        .organisasjonsnummer(part.getOrganizationNumber())
                        .organisasjonsform(part.getUnitType())
                        .build())
                .toList());

        return Mono.just(organisasjoner);
    }
}