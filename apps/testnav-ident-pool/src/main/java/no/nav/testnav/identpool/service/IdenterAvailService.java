package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.util.IdentGeneratorUtility;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdenterAvailService {

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    public Flux<String> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        var oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);

        return Mono.just(IdentGeneratorUtility.genererIdenter(oppdatertRequest, new HashSet<>()))
                .flatMapMany(genererteIdenter -> identRepository.findByPersonidentifikatorIn(genererteIdenter)
                        .collectList()
                        .flatMap(databaseIdenter -> Mono.just(filtrerIdenter(genererteIdenter, databaseIdenter)))
                        .map(Flux::fromIterable))
                .flatMap(Flux::from);
    }

    private static Set<String> filtrerIdenter(Set<String> opprettedeIdenter, List<Ident> databaseIdenter) {

        return opprettedeIdenter.stream()
                .filter(ident -> databaseIdenter.stream()
                        .map(Ident::getPersonidentifikator)
                        .noneMatch(ident::equals))
                .collect(Collectors.toSet());
    }
}
