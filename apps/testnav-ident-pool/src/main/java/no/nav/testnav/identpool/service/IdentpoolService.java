package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.testnav.identpool.providers.v1.support.MarkerBruktRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.util.PersonidentUtil;
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

import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.PersonidentUtil.getIdentType;
import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;
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

    public Mono<Ident> markerBrukt(MarkerBruktRequest request) {

        return identRepository.findByPersonidentifikator(request.getPersonidentifikator())
                .flatMap(ident -> {
                    if (ident.getRekvireringsstatus() == LEDIG) {
                            ident.setRekvireringsstatus(I_BRUK);
                            ident.setRekvirertAv(request.getBruker());
                            return identRepository.save(ident);
                        } else {
                            return(Mono.error(new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som i bruk.")));
                        }
                    })
                .switchIfEmpty(Mono.just(Ident.builder()
                        .identtype(getIdentType(request.getPersonidentifikator()))
                        .personidentifikator(request.getPersonidentifikator())
                        .rekvireringsstatus(I_BRUK)
                        .rekvirertAv(request.getBruker())
                        .kjoenn(PersonidentUtil.getKjonn(request.getPersonidentifikator()))
                        .foedselsdato(PersonidentUtil.toBirthdate(request.getPersonidentifikator()))
                        .syntetisk(isSyntetisk(request.getPersonidentifikator()))
                        .build())
                        .flatMap(identRepository::save));
    }
}
