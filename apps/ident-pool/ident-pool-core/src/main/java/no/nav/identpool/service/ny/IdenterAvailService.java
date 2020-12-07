package no.nav.identpool.service.ny;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.repository.postgres.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.service.IdentGeneratorService;

@Service
@RequiredArgsConstructor
public class IdenterAvailService {

    private static final int MAX_TPS_CALL_ATTEMPTS = 3;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;

    public List<TpsStatus> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        HentIdenterRequest oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);
        List<TpsStatus> tpsStatuser = new ArrayList<>();
        int i = 0;

        while (i < MAX_TPS_CALL_ATTEMPTS &&
                tpsStatuser.stream().filter(status -> !status.isInUse()).count() < request.getAntall()) {

            List<String> genererteIdenter = genererIdenter(oppdatertRequest);
            List<Ident> identerFinnesIdb = identRepository.findByPersonidentifikatorIn(genererteIdenter);

            List<String> identerAaSjekke = genererteIdenter.stream()
                    .filter(ident -> identerFinnesIdb.stream()
                            .noneMatch(dbIdent -> dbIdent.getPersonidentifikator().equals(ident)))
                    .collect(Collectors.toList());

            if (!identerAaSjekke.isEmpty()) {
                tpsStatuser.addAll(tpsfService.checkAvailStatus(identerAaSjekke));
            }
        }

        return tpsStatuser;
    }

    private List<String> genererIdenter(HentIdenterRequest request) {

        if (request.getIdenttype() == Identtype.FDAT) {
            return identGeneratorService.genererIdenterFdat(request, emptyList());
        } else {
            return identGeneratorService.genererIdenter(request, emptyList());
        }
    }
}
