package no.nav.testnav.altinn3tilgangservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.AltinnConsumer;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AuthorizedPartyDTO;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnBrukerTilgangService {

    private static final String DOLLY_RESOURCE = "nav_dolly_tilgang-samarbeidspartnere";
    private final AltinnConsumer altinnConsumer;

    public Flux<OrganisasjonDTO> getPersonOrganisasjonTilgang(String ident) {

        return Flux.zip(
                        altinnConsumer.getAuthorizedParties(ident),
                        altinnConsumer.getOrganisasjoner().collectList())
                .flatMap(this::getOrganisasjon);
    }

    private Mono<OrganisasjonDTO> getOrganisasjon(Tuple2<AuthorizedPartyDTO, List<Organisasjon>> organisasjoner) {

        return Mono.just(organisasjoner.getT1())
                .filter(party -> party.getAuthorizedResources().contains(DOLLY_RESOURCE))
                .filter(party -> organisasjoner.getT2().stream()
                        .anyMatch(organisasjon -> organisasjon.getOrganisasjonsnummer().equals(party.getOrganizationNumber())))
                .map(part -> OrganisasjonDTO.builder()
                        .navn(part.getName())
                        .organisasjonsnummer(part.getOrganizationNumber())
                        .organisasjonsform(part.getUnitType())
                        .build());
    }
}
