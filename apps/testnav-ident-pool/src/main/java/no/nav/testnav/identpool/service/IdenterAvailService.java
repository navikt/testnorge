package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
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

    private final IdentGeneratorService identGeneratorService;
    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Flux<String> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        var oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);

        return Mono.just(identGeneratorService.genererIdenter(oppdatertRequest))
                .flatMapMany(genererteIdenter -> identRepository.findByPersonidentifikatorIn(genererteIdenter)
                        .collectList()
                        .flatMap(databaseIdenter -> Mono.just(filtrerIdenter(genererteIdenter, databaseIdenter)))
                        .map(Flux::fromIterable))
                .flatMap(Flux::from)
                .collectList()
                .flatMap(opprettedeIdenter -> isTrue(request.getSyntetisk()) ?
                        Mono.just(opprettedeIdenter) :
                        tpsMessagingConsumer.getIdenterProdStatus(getSubSet(opprettedeIdenter))
                                .filter(TpsStatusDTO::isAvailable)
                                .map(TpsStatusDTO::getIdent)
                                .collectList())
                .flatMapMany(Flux::fromIterable);
    }

    private static Set<String> getSubSet(List<String> opprettedeIdenter) {

        return new HashSet<>(opprettedeIdenter.subList(0, Math.min(opprettedeIdenter.size(), TpsMessagingConsumer.PAGESIZE)));
    }

    private static Set<String> filtrerIdenter(Set<String> opprettedeIdenter, List<Ident> databaseIdenter) {

        return opprettedeIdenter.stream()
                .filter(ident -> databaseIdenter.stream()
                        .map(Ident::getPersonidentifikator)
                        .noneMatch(ident::equals))
                .collect(Collectors.toSet());
    }
}
