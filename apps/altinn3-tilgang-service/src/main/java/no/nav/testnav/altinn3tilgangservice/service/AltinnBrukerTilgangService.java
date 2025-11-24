package no.nav.testnav.altinn3tilgangservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.AltinnConsumer;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.AuthorizedPartyDTO;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.altinn3.v1.PersonDTO;
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
                        altinnConsumer.getAuthorizedParties(ident).collectList(),
                        altinnConsumer.getOrganisasjoner().collectList())
                .flatMap(this::getOrganisasjon);
    }

    private Flux<OrganisasjonDTO> getOrganisasjon(Tuple2<List<AuthorizedPartyDTO>, List<Organisasjon>> organisasjoner) {

        return Flux.fromIterable(organisasjoner.getT1())
                .doOnNext(org -> log.info("Organisasjon {}", org))
                .filter(party -> party.getAuthorizedResources().contains(DOLLY_RESOURCE))
                .filter(party -> organisasjoner.getT2().stream()
                        .anyMatch(organisasjon -> organisasjon.getOrganisasjonsnummer().equals(party.getOrganizationNumber())))
                .map(part -> OrganisasjonDTO.builder()
                        .navn(part.getName())
                        .organisasjonsnummer(part.getOrganizationNumber())
                        .organisasjonsform(part.getUnitType())
                        .build());
    }

    public Mono<PersonDTO> getPersonOrganisasjonDetaljertTilgang(String ident) {

        return Mono.zip(
                        altinnConsumer.getAuthorizedParties(ident).collectList(),
                        altinnConsumer.getOrganisasjoner().collectList())
                .flatMapMany(this::getTilpassetOrganisasjon)
                .collectList()
                .map(organisasjoner -> PersonDTO.builder()
                        .ident(ident)
                        .organisasjoner(organisasjoner)
                        .build());
    }

    private Flux<PersonDTO.OrganisasjonDTO> getTilpassetOrganisasjon(Tuple2<List<AuthorizedPartyDTO>, List<Organisasjon>> organisasjoner) {

        return Flux.fromIterable(organisasjoner.getT1())
                .map(party -> PersonDTO.OrganisasjonDTO.builder()
                        .navn(party.getName())
                        .organisasjonsnummer(party.getOrganizationNumber())
                        .organisasjonsform(party.getUnitType())
                        .hasAltinnDollyTilgang(hasAltinnDollyTilgang(party))
                        .hasDollyOrganisasjonTilgang(hasDollyOrganisasjonTilgang(organisasjoner.getT2(), party))
                        .melding(getMelding(party.getName(), party.getOrganizationNumber(),
                                hasAltinnDollyTilgang(party), hasDollyOrganisasjonTilgang(organisasjoner.getT2(), party)))
                        .build());
    }

    private static boolean hasAltinnDollyTilgang(AuthorizedPartyDTO authorizedParty) {

        return authorizedParty.getAuthorizedResources().contains(DOLLY_RESOURCE);
    }

    private static boolean hasDollyOrganisasjonTilgang(List<Organisasjon> organisasjoner, AuthorizedPartyDTO party) {

        return organisasjoner.stream()
                .anyMatch(organisasjon -> organisasjon.getOrganisasjonsnummer().equals(party.getOrganizationNumber()));
    }

    private static String getMelding(String orgnavn, String orgnummer, boolean hasAltinnDollyTilgang, boolean hasDollyOrganisasjonTilgang) {

        return new StringBuilder()
                .append(!hasAltinnDollyTilgang ?
                        "Du mangler tilgang i Altinn på følgende tjenste: " +
                                "\"Tilgang til NAVs Dolly for samarbeidspartnere\" " +
                                "for organisasjon %s (med orgnummer %s)%n".formatted(orgnavn, orgnummer) : "")
                .append(!hasAltinnDollyTilgang && !hasDollyOrganisasjonTilgang ?
                        " og %n" : "")
                .append(!hasDollyOrganisasjonTilgang ?
                        "Organisasjon %s (med orgnummer %s) " .formatted(orgnavn, orgnummer) +
                                "mangler tilgang på Dolly syntetiske testdata selvbetjening%n" : "")
                .append(!hasAltinnDollyTilgang || !hasDollyOrganisasjonTilgang ?
                        "Hvis du har til hensikt å bruke Dolly til å generere testdata, gjør følgende:%n" : "")
                .append(!hasAltinnDollyTilgang ?
                        "- Ta kontakt med Altinn-ansvarlig i %s (med orgnummer %s) ".formatted(orgnavn, orgnummer) +
                                "og spør om vedkommene kan gi deg tilgang til tjenesten: " +
                                "\"Tilgang til NAVs Dolly for samarbeidspartnere\"%n" : "")
                .append(!hasDollyOrganisasjonTilgang ?
                        "- Ta kontakt med NAV ved Anders Marstrander epost: anders.marstrander@nav.no, og spør om " +
                                "organisasjon med orgnr %s kan gis tilgang til Dolly syntetiske testdata selvbetjening%n".formatted(orgnummer) : "")
                .toString();
    }
}
