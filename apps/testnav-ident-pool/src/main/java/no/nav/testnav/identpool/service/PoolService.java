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

import java.util.List;

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

    private static void throwException(HentIdenterRequest request) {
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

    public synchronized List<String> allocateIdenter(HentIdenterRequest request) {

        var identEntities = databaseService.hentLedigeIdenterFraDatabase(request);
        int missingIdentCount = request.getAntall() - identEntities.size();

        if (missingIdentCount > 0) {

            var statusDTOS = identerAvailService.generateAndCheckIdenter(request,
                    isTrue(request.getSyntetisk()) ? ATTEMPT_OBTAIN * 12 : ATTEMPT_OBTAIN);

            log.info("Generert {} identer ved mining", statusDTOS.size());

            var identerFraMining = statusDTOS.stream()
                    .map(this::buildIdent)
                    .toList();
            identRepository.saveAll(identerFraMining);

            var ledigeIdents = identerFraMining.stream()
                    .filter(Ident::isLedig)
                    .toList().iterator();

            int i = 0;
            while (i < missingIdentCount && ledigeIdents.hasNext()) {
                identEntities.add(ledigeIdents.next());
                i++;
            }

            logRequest(request);

            if (identEntities.size() < request.getAntall()) {
                throwException(request);
            }
        }

        identRepository.saveAll(identEntities.stream()
                .map(ident -> {
                    ident.setRekvireringsstatus(I_BRUK);
                    ident.setRekvirertAv(request.getRekvirertAv());
                    return ident;
                })
                .toList());

        return identEntities.stream().map(Ident::getPersonidentifikator).toList();
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
