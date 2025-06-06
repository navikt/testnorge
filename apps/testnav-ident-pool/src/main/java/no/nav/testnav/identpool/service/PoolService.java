package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Objects.nonNull;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.DatoFraIdentUtility.getFoedselsdato;
import static no.nav.testnav.identpool.util.IdenttypeFraIdentUtility.getIdenttype;
import static no.nav.testnav.identpool.util.KjoennFraIdentUtility.getKjoenn;
import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolService {

    private static final int ATTEMPT_OBTAIN = 480;
    private final IdenterAvailService identerAvailService;
    private final DatabaseService databaseService;
    private final IdentRepository identRepository;

    private static Throwable throwException(HentIdenterRequest request) {

        throw new ForFaaLedigeIdenterException(format("Identpool finner ikke ledige identer i hht forespørsel: " +
                        "identType %s, kjønn %s, fødtEtter %s, fødtFør %s, syntetisk %b -- "
                        + "forsøk å bestille med andre kriterier.",
                nonNull(request.getIdenttype()) ? request.getIdenttype().name() : null,
                nonNull(request.getKjoenn()) ? request.getKjoenn().name() : null,
                nonNull(request.getFoedtEtter()) ? request.getFoedtEtter().format(ISO_DATE) : null,
                nonNull(request.getFoedtFoer()) ? request.getFoedtFoer().format(ISO_DATE) : null,
                request.getSyntetisk()));
    }

    private static void logRequest(HentIdenterRequest request) {

        log.info("Leverte identer: antall {}, rekvirertAv {}, identType {}, kjønn {}, fødtEtter {}, fødtFør {}, syntetisk {}",
                request.getAntall(), request.getRekvirertAv(),
                nonNull(request.getIdenttype()) ? request.getIdenttype().name() : null,
                nonNull(request.getKjoenn()) ? request.getKjoenn().name() : null,
                nonNull(request.getFoedtEtter()) ? request.getFoedtEtter().format(ISO_DATE) : null,
                nonNull(request.getFoedtFoer()) ? request.getFoedtFoer().format(ISO_DATE) : null,
                isTrue(request.getSyntetisk()));
    }

    public synchronized Mono<List<String>> allocateIdenter(HentIdenterRequest request) {

        var counter = request.getAntall() * 2;

        return databaseService.hentLedigeIdenterFraDatabase(request)
                .collectList()
                .flatMap(ledigeIdenter -> {
                    if (ledigeIdenter.size() < request.getAntall()) {
                        return generateIdenter(request, counter);
                    } else {
                        return oppdaterIBruk(request, ledigeIdenter);
                    }
                })
                .switchIfEmpty(generateIdenter(request, counter))
                .doOnNext(identer -> logRequest(request));
    }

    private Mono<List<String>> generateIdenter(HentIdenterRequest request, int antall) {

        return identerAvailService.generateAndCheckIdenter(request, ATTEMPT_OBTAIN)
                .take(antall)
                .map(this::buildIdentLedig)
                .flatMap(identRepository::save)
                .collectList()
                .doOnNext(identer -> log.info("Antall identer allokert: {}", identer.size()))
                .doOnNext(identer -> {
                    if (identer.size() < request.getAntall()) {
                        log.warn("For få identer allokert: {} for rekvirering: {}", identer.size(), request);
                        throwException(request);
                    }
                })
                .flatMap(identer -> oppdaterIBruk(request, identer));
    }

    private Mono<List<String>> oppdaterIBruk(HentIdenterRequest request, List<Ident> ledige) {

        return Flux.fromIterable(ledige)
                .take(request.getAntall())
                .map(ident -> {
                    ident.setRekvirertAv(request.getRekvirertAv());
                    ident.setRekvireringsstatus(I_BRUK);
                    return ident;
                })
                .flatMap(identRepository::save)
                .map(Ident::getPersonidentifikator)
                .collectList();
    }

    private Ident buildIdentLedig(String ident) {

        return Ident.builder()
                .personidentifikator(ident)
                .foedselsdato(getFoedselsdato(ident))
                .kjoenn(getKjoenn(ident))
                .identtype(getIdenttype(ident))
                .rekvireringsstatus(LEDIG)
                .syntetisk(isSyntetisk(ident))
                .build();
    }
}
