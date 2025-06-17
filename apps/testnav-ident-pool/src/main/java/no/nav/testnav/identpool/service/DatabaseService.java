package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Random;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {

    private static final Random RANDOM = new SecureRandom();

    private final IdentRepository identRepository;
    private final MapperFacade mapperFacade;

    public Flux<Ident> hentLedigeIdenterFraDatabase(HentIdenterRequest request) {

        var availableIdentsRequest = mapperFacade.map(request, HentIdenterRequest.class);

        return getAntall(availableIdentsRequest)
                .doOnNext(availableIdents -> log.info("Antall ledige identer: {}", availableIdents))
                .flatMapMany(antall -> {
                    if (antall > 0) {
                        return getPage(request,
                                PageRequest.of(RANDOM.nextInt((antall > request.getAntall() ? antall : request.getAntall()) / request.getAntall()), request.getAntall()));
                    } else {
                        return Flux.empty();
                    }
                });
    }

    private Mono<Integer> getAntall(HentIdenterRequest request) {

        return nonNull(request.getKjoenn()) ?

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
                                Rekvireringsstatus.LEDIG, request.getIdenttype(),
                                isTrue(request.getSyntetisk()), request.getKjoenn(),
                                request.getFoedtEtter(), request.getFoedtFoer())
                        .doOnNext(antall -> log.info("Funnet antall ledige inkl kjoenn: {}", antall))

                :

                identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                                Rekvireringsstatus.LEDIG, request.getIdenttype(),
                                isTrue(request.getSyntetisk()),
                                request.getFoedtEtter(), request.getFoedtFoer())
                        .doOnNext(antall -> log.info("Funnet antall ledige uten spesifisert kjoenn: {}", antall));
    }

    private Flux<Ident> getPage(HentIdenterRequest request, Pageable page) {

        return nonNull(request.getKjoenn()) ?

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetweenOrderByFoedselsdato(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()), request.getKjoenn(),
                        request.getFoedtEtter(), request.getFoedtFoer(), page)

                :

                identRepository.findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetweenOrderByFoedselsdato(
                        Rekvireringsstatus.LEDIG, request.getIdenttype(),
                        isTrue(request.getSyntetisk()),
                        request.getFoedtEtter(), request.getFoedtFoer(), page);
    }
}
