package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private static final Random RANDOM = new SecureRandom();

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    public Mono<List<Ident>> hentLedigeIdenterFraDatabase(HentIdenterRequest request) {

        var availableIdentsRequest = mapperFacade.map(request, HentIdenterRequest.class);

        return getAntall(availableIdentsRequest)
                .flatMap(antall -> getPage(request, PageRequest.of(RANDOM.nextInt(antall/request.getAntall()), request.getAntall())))
                .map(Slice::getContent);
    }

    private Mono<Integer> getAntall(HentIdenterRequest request) {

        return nonNull(request.getKjoenn()) ?

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer()) :

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                                request.getFoedtEtter(), request.getFoedtFoer());
    }

    private Mono<Page<Ident>> getPage(HentIdenterRequest request, Pageable page) {

        return nonNull(request.getKjoenn()) ?

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer(), page
                ) :

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                        request.getFoedtEtter(), request.getFoedtFoer(), page
                );
    }
}
