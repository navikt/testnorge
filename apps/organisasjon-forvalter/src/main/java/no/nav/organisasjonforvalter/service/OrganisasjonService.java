package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisasjonService {

    private final MapperFacade mapperFacade;
    private final OrganisasjonRepository organisasjonRepository;

    public Mono<List<RsOrganisasjon>> getOrganisasjoner(List<String> orgnumre) {

        return Mono.fromCallable(() -> mapperFacade.mapAsList(organisasjonRepository.findAllByOrganisasjonsnummerIn(orgnumre), RsOrganisasjon.class))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<RsOrganisasjon>> getOrganisasjoner(String brukerid) {

        return Mono.fromCallable(() -> mapperFacade.mapAsList(organisasjonRepository.findOrganisasjonerByBrukerId(brukerid), RsOrganisasjon.class))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
