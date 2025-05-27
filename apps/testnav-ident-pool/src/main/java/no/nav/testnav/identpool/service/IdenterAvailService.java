package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
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

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class IdenterAvailService {

    private static final int MAX_TPS_CALL_ATTEMPTS = 1;

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final MapperFacade mapperFacade;

    public Flux<TpsStatusDTO> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        var oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);

        return Flux.range(0, MAX_TPS_CALL_ATTEMPTS)
                .flatMap(i -> Mono.just(IdentGeneratorUtility.genererIdenter(request, new HashSet<>()))
                        .flatMapMany(genererteIdenter -> identRepository.findByPersonidentifikatorIn(genererteIdenter)
                                .collectList()
                                .map(databaseIdenter -> filtrerIdenter(genererteIdenter, databaseIdenter))
                                .map(identerAaSjekke -> {
                                    if (isTrue(request.getSyntetisk())) {
                                        return Flux.fromIterable(identerAaSjekke)
                                                .map(ident -> TpsStatusDTO.builder()
                                                        .ident(ident)
                                                        .inUse(false)
                                                        .build());
                                    } else {
                                        return tpsMessagingConsumer.getIdenterStatuser(new HashSet<>(identerAaSjekke));
                                    }
                                })))
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
