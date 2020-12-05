package no.nav.identpool.service.ny;

import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;

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

    public List<String> allocateIdenter(HentIdenterRequest request) {

        Set<Ident> identEntities = databaseService.hentLedigeIdenterFraDatabase(request);

        int missingIdentCount = request.getAntall() - identEntities.size();

        if (missingIdentCount > 0) {

            List<TpsStatus> tpsStatuses = identerAvailService.generateAndCheckIdenter(request, ATTEMPT_OBTAIN);

            List<Ident> identerFraTps = tpsStatuses.stream()
                    .map(status -> buildIdent(status))
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

            if (identEntities.size() < request.getAntall()) {
                throw new ForFaaLedigeIdenterException("Det er for få ledige identer i TPS - prøv med et annet dato-intervall.");
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

    private static Rekvireringsstatus getRekvireringsstatus(boolean inUse) {
        return inUse ? I_BRUK : LEDIG;
    }
}
