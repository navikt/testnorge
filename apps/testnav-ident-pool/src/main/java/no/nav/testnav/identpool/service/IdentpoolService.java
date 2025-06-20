package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentpoolService {

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Flux<TpsStatusDTO> finnesIProd(Set<String> identer) {

        return tpsMessagingConsumer.getIdenterProdStatus(identer);
    }

    @Transactional
    public Mono<List<String>> frigjoerIdenter(List<String> identer) {

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
                .map(Ident::getPersonidentifikator)
                .collectList();
    }

    public Mono<Ident> lesInnhold(String ident) {

        return identRepository.findByPersonidentifikator(ident)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Fant ikke ident %s".formatted(ident))));
    }

    public Mono<List<String>> hentLedigeFNRFoedtMellom(LocalDate from, LocalDate to, Boolean syntetisk) {

        return identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(from, to,
                        Identtype.FNR, LEDIG, isTrue(syntetisk))
                .map(Ident::getPersonidentifikator)
                .collectList();
    }

    public Mono<Boolean> erLedig(String personidentifikator) {

        return identRepository.findByPersonidentifikator(personidentifikator)
                .flatMap(testident -> {
                    if (testident.getRekvireringsstatus().equals(Rekvireringsstatus.I_BRUK)) {
                        return Mono.just(false);
                    } else if (testident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) &&
                            personidentifikator.getBytes(StandardCharsets.UTF_8)[2] >= '4') {
                        return Mono.just(true);
                    } else {
                        return tpsMessagingConsumer.getIdenterProdStatus(Collections.singleton(personidentifikator))
                                .map(status -> !status.isInUse())
                                .next();
                    }
                })
                .switchIfEmpty(personidentifikator.getBytes(StandardCharsets.UTF_8)[2] >= '4'
                        ? Mono.just(true)
                        : tpsMessagingConsumer.getIdenterProdStatus(Collections.singleton(personidentifikator))
                        .map(status -> !status.isInUse())
                        .next());
    }
}
