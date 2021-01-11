package no.nav.identpool.service.ny;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;

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

    public synchronized List<String> allocateIdenter(HentIdenterRequest request) {

        Set<Ident> identEntities = databaseService.hentLedigeIdenterFraDatabase(request);
        int missingIdentCount = request.getAntall() - identEntities.size();

        if (missingIdentCount > 0) {

            Set<TpsStatus> tpsStatuses = identerAvailService.generateAndCheckIdenter(request, ATTEMPT_OBTAIN);

            List<Ident> identerFraTps = tpsStatuses.stream()
                    .map(this::buildIdent)
                    .collect(Collectors.toList());
            identRepository.saveAll(identerFraTps);

            Iterator<Ident> ledigeIdents = identerFraTps.stream()
                    .filter(Ident::isLedig)
                    .collect(Collectors.toList()).iterator();

            int i = 0;
            while (i < missingIdentCount && ledigeIdents.hasNext()) {
                identEntities.add(ledigeIdents.next());
                i++;
            }

            log.info("Leverte identer: antall {}, rekvirertAv {}, identType {}, kjønn {}, fødtEtter {}, fødtFør {}",
                    request.getAntall(), request.getRekvirertAv(), request.getIdenttype().name(), request.getKjoenn().name(),
                    request.getFoedtEtter().format(ISO_DATE), request.getFoedtFoer().format(ISO_DATE));

            if (identEntities.size() < request.getAntall()) {
                throw new ForFaaLedigeIdenterException(format("Identpool finner ikke ledige identer i hht forespørsel: " +
                                "identType %s, kjønn %s, fødtEtter %s, fødtFør %s. \nForsøk å bestille med andre kriterier.",
                        request.getIdenttype(), request.getKjoenn(),
                        request.getFoedtEtter().format(ISO_DATE),
                        request.getFoedtFoer().format(ISO_DATE)));
            }
        }

        identRepository.saveAll(identEntities.stream()
                .map(ident -> {
                    ident.setRekvireringsstatus(I_BRUK);
                    ident.setRekvirertAv(request.getRekvirertAv());
                    return ident;
                })
                .collect(Collectors.toList()));

        return identEntities.stream().map(Ident::getPersonidentifikator).collect(Collectors.toList());
    }

    private Ident buildIdent(TpsStatus tpsStatus) {
        return buildIdent(tpsStatus.getIdent(), "TPS", getRekvireringsstatus(tpsStatus.isInUse()));
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
                .build();
    }
}
