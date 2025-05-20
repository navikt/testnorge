package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.testnav.identpool.providers.v1.support.MarkerBruktRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.util.IdentGeneratorUtil;
import no.nav.testnav.identpool.util.PersonidentUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.PersonidentUtil.getIdentType;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentpoolService {

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Mono<Boolean> erLedig(String personidentifikator) {

        return identRepository.findByPersonidentifikator(personidentifikator)
                .flatMap(ident -> {
                    if (LEDIG == ident.getRekvireringsstatus()) {
                        return Mono.just(true);
                    } else {
                        return tpsMessagingConsumer.getIdenterStatuser(Collections.singleton(personidentifikator))
                                .map(TpsStatusDTO::isInUse)
                                .reduce(false, (a, b) -> a || b);
                    }
                });
    }

    public Flux<TpsStatusDTO> finnesIProd(Set<String> identer) {

        return tpsMessagingConsumer.getIdenterProdStatus(identer)
                .filter(TpsStatusDTO::isInUse);
    }

    @Transactional
    public Flux<String> frigjoerIdenter(List<String> identer) {

        return identRepository.findByPersonidentifikatorIn(identer)
                .map(ident -> {
                    ident.setRekvireringsstatus(LEDIG);
                    ident.setRekvirertAv(null);
                    return ident;
                })
                .collectList()
                .doOnNext(frigjorteIdenter -> log.info("Frigjorte identer: {}",
                        frigjorteIdenter.stream().map(Ident::getPersonidentifikator).collect(Collectors.joining(","))))
                .flatMapMany(Flux::fromIterable)
                .flatMap(identRepository::save)
                .map(Ident::getPersonidentifikator);
    }

    public Mono<Ident> lesInnhold(String personidentifikator) {

        return identRepository.findByPersonidentifikator(personidentifikator);
    }

    public Mono<List<String>> hentLedigeFNRFoedtMellom(LocalDate from, LocalDate to) {

        return identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(from, to,
                        Identtype.FNR, LEDIG, false)
                .map(Ident::getPersonidentifikator)
                .collectList();
    }
}
