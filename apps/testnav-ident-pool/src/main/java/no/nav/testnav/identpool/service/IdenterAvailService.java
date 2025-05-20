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
import java.util.Set;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class IdenterAvailService {

    private static final int MAX_TPS_CALL_ATTEMPTS = 1;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final MapperFacade mapperFacade;

    public Flux<TpsStatusDTO> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        var oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);

        return Flux.range(0, MAX_TPS_CALL_ATTEMPTS)
                .flatMap(i -> Mono.just(genererIdenter(oppdatertRequest))
                        .flatMapMany(genererteIdenter -> identRepository.findByPersonidentifikatorIn(genererteIdenter)
                                .collectList()
                                .filter(identerFinnesIdb -> genererteIdenter.stream()
                                        .noneMatch(generertIdent -> identerFinnesIdb.stream()
                                                .anyMatch(dbIdent -> dbIdent.getPersonidentifikator().equals(generertIdent))))
                                .flatMapMany(Flux::fromIterable)
                                .map(Ident::getPersonidentifikator)
                                .collectList()
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

    private Set<String> genererIdenter(HentIdenterRequest request) {

        return identGeneratorService.genererIdenter(request, new HashSet<>());
    }
}
