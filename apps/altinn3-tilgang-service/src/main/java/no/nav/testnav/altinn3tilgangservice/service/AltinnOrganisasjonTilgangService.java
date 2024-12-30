package no.nav.testnav.altinn3tilgangservice.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.AltinnConsumer;
import no.nav.testnav.altinn3tilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.altinn3tilgangservice.database.repository.OrganisasjonTilgangRepository;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import no.nav.testnav.altinn3tilgangservice.domain.OrganisasjonResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class AltinnOrganisasjonTilgangService {

    private static final String ORGANISASJON_TILGANG = "tilgang";
    private final AltinnConsumer altinnConsumer;
    private final OrganisasjonTilgangRepository organisasjonTilgangRepository;
    private final MapperFacade mapperFacade;

    public Flux<OrganisasjonResponse> getAll() {

        return altinnConsumer.getOrganisasjoner()
                .flatMap(this::convertResponse);
    }

    public Mono<OrganisasjonResponse> create(String orgnummer, String miljoe) {

        return altinnConsumer.create(orgnummer)
                .flatMap(altinnOrg -> {
                    if (isBlank(altinnOrg.getFeilmelding())) {
                        return saveOrganisasjon(orgnummer, miljoe)
                                .map(tilgang -> {
                                    var context = new MappingContext.Factory().getContext();
                                    context.setProperty(ORGANISASJON_TILGANG, tilgang);
                                    return mapperFacade.map(altinnOrg, OrganisasjonResponse.class, context);
                                });
                    } else {
                        var context = new MappingContext.Factory().getContext();
                        context.setProperty(ORGANISASJON_TILGANG, OrganisasjonTilgang.builder()
                                .organisasjonNummer(orgnummer)
                                .miljoe(miljoe)
                                .build());
                        return Mono.just(mapperFacade.map(altinnOrg, OrganisasjonResponse.class, context));
                    }
                });
    }

    public Flux<OrganisasjonResponse> delete(String organisasjonsnummer) {

        return organisasjonTilgangRepository.deleteByOrganisasjonNummer(organisasjonsnummer)
                .flatMapMany(result -> altinnConsumer.delete(organisasjonsnummer))
                .flatMap(this::convertResponse);
    }

    private Mono<OrganisasjonResponse> convertResponse(Organisasjon organisasjon) {

        return organisasjonTilgangRepository
                .existsByOrganisasjonNummer(organisasjon.getOrganisasjonsnummer())
                .flatMap(exists -> isTrue(exists) ?
                        organisasjonTilgangRepository
                                .findByOrganisasjonNummer(organisasjon.getOrganisasjonsnummer()) :
                        Mono.just(new OrganisasjonTilgang()))
                .map(organisasjonTilgang -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty(ORGANISASJON_TILGANG, organisasjonTilgang);
                    return mapperFacade.map(organisasjon, OrganisasjonResponse.class, context);
                });
    }

    private Mono<OrganisasjonTilgang> saveOrganisasjon(String orgnummer, String miljoe) {

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
}
