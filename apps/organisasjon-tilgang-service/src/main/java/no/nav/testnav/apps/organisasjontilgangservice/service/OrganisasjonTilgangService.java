package no.nav.testnav.apps.organisasjontilgangservice.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.AltinnConsumer;
import no.nav.testnav.apps.organisasjontilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.apps.organisasjontilgangservice.database.repository.OrganisasjonTilgangRepository;
import no.nav.testnav.apps.organisasjontilgangservice.domain.OrganisasjonResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganisasjonTilgangService {

    private final AltinnConsumer altinnConsumer;
    private final OrganisasjonTilgangRepository organisasjonTilgangRepository;
    private final MapperFacade mapperFacade;

    public Flux<OrganisasjonResponse> getAll() {

        return altinnConsumer.getOrganisasjoner()
                .flatMap(organisasjon -> organisasjonTilgangRepository
                        .existsByOrganisasjonNummer(organisasjon.getOrganisasjonsnummer())
                        .map(exists -> exists ?
                                organisasjonTilgangRepository
                                        .findByOrganisasjonNummer(organisasjon.getOrganisasjonsnummer()) :
                                Mono.just(new OrganisasjonTilgang()))
                        .map(organisasjonTilgang -> {
                            var context = new MappingContext.Factory().getContext();
                            context.setProperty("organisasjonTilgang", organisasjonTilgang);
                            return mapperFacade.map(organisasjon, OrganisasjonResponse.class);
                        }));
    }

    public Mono<OrganisasjonResponse> create(String organisasjonsnummer, LocalDateTime gyldigTil, String miljoe) {

        return organisasjonTilgangRepository.existsByOrganisasjonNummer(organisasjonsnummer)
                .flatMap(exists -> exists ?
                        organisasjonTilgangRepository.findByOrganisasjonNummer(organisasjonsnummer) :
                        Mono.just(OrganisasjonTilgang.builder()
                                .organisasjonNummer(organisasjonsnummer)
                                .build()))
                .flatMap(organisasjon -> {
                    organisasjon.setMiljoe(miljoe);
                    return organisasjonTilgangRepository.save(organisasjon);
                })
                .flatMap(organisasjonTilgang -> altinnConsumer.create(organisasjonsnummer, gyldigTil)
                        .flatMap(organisasjon -> organisasjonTilgangRepository
                                .findByOrganisasjonNummer(organisasjon.getOrganisasjonsnummer())
                                .map(tilgang -> {
                                    var context = new MappingContext.Factory().getContext();
                                    context.setProperty("organisasjonTilgang", tilgang);
                                    return mapperFacade.map(organisasjon, OrganisasjonResponse.class, context);
                                })));
    }

    public Flux<Void> delete(String organisasjonsnummer) {

        return altinnConsumer.delete(organisasjonsnummer)
                .flatMap(status -> organisasjonTilgangRepository.deleteByOrganisasjonNummer(organisasjonsnummer));
    }
}
