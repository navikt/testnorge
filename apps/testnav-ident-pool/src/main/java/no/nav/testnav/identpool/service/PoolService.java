package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Objects.nonNull;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolService {

    private static final int ATTEMPT_OBTAIN = 40;
    private final IdenterAvailService identerAvailService;
    private final DatabaseService databaseService;
    private final IdentRepository identRepository;
    private final DatoFraIdentService datoFraIdentService;
    private final KjoennFraIdentService kjoennFraIdentService;
    private final IdenttypeFraIdentService identtypeFraIdentService;

    private static Rekvireringsstatus getRekvireringsstatus(boolean inUse) {
        return inUse ? I_BRUK : LEDIG;
    }

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

        var counter = new AtomicInteger(request.getAntall());

        return databaseService.hentLedigeIdenterFraDatabase(request)
                .flatMap(ledigeIdenter -> {
                    if (ledigeIdenter.size() < request.getAntall()) {
                        return identerAvailService.generateAndCheckIdenter(request,
                                        isTrue(request.getSyntetisk()) ? ATTEMPT_OBTAIN * 12 : ATTEMPT_OBTAIN)
                                .map(this::buildIdent)
                                .collectList()
                                .flatMapMany(identRepository::saveAll)
                                .filter(ident -> ident.getRekvireringsstatus() == LEDIG)
                                .takeWhile(ident -> counter.getAndDecrement() > 0)
                                .collectList()
                                .flatMap(ledige -> {
                                    if (ledige.size() < request.getAntall()) {
                                        return Mono.error(throwException(request));
                                    } else {
                                        logRequest(request);
                                        return getListMono(request, ledige);
                                    }
                                });
                    } else {
                        return getListMono(request, ledigeIdenter);
                    }
                });
    }

    private Mono<List<String>> getListMono(HentIdenterRequest request, List<Ident> ledige) {

        return Flux.fromIterable(ledige)
                .map(ident -> {
                    ident.setRekvireringsstatus(I_BRUK);
                    ident.setRekvirertAv(request.getRekvirertAv());
                    return ident;
                })
                .collectList()
                .flatMapMany(identRepository::saveAll)
                .map(Ident::getPersonidentifikator)
                .collectList();
    }

    private Ident buildIdent(TpsStatusDTO tpsStatusDTO) {
        return buildIdent(tpsStatusDTO.getIdent(), "TPS", getRekvireringsstatus(tpsStatusDTO.isInUse()));
    }

    private Ident buildIdent(String ident, String rekvirertAv, Rekvireringsstatus rekvireringsstatus) {
        return Ident.builder()
                .personidentifikator(ident)
                .finnesHosSkatt(false)
                .foedselsdato(datoFraIdentService.getFoedselsdato(ident))
                .kjoenn(kjoennFraIdentService.getKjoenn(ident))
                .identtype(identtypeFraIdentService.getIdenttype(ident))
                .rekvirertAv(rekvireringsstatus == LEDIG ? null : rekvirertAv)
                .rekvireringsstatus(rekvireringsstatus)
                .syntetisk(isSyntetisk(ident))
                .build();
    }
}
