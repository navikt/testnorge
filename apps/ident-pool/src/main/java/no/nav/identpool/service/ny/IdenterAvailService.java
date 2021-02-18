package no.nav.identpool.service.ny;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.service.IdentGeneratorService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdenterAvailService {

    private static final int MAX_TPS_CALL_ATTEMPTS = 1;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;

    public Set<TpsStatus> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        HentIdenterRequest oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);
        Set<TpsStatus> tpsStatuser = new HashSet<>();
        int i = 0;

        while (i < MAX_TPS_CALL_ATTEMPTS &&
                tpsStatuser.stream().filter(status -> !status.isInUse()).count() < request.getAntall()) {

            Set<String> genererteIdenter = genererIdenter(oppdatertRequest);
            Set<Ident> identerFinnesIdb = identRepository.findByPersonidentifikatorIn(genererteIdenter);

            Set<String> identerAaSjekke = genererteIdenter.stream()
                    .filter(ident -> identerFinnesIdb.stream()
                            .noneMatch(dbIdent -> dbIdent.getPersonidentifikator().equals(ident)))
                    .collect(Collectors.toSet());

            if (!identerAaSjekke.isEmpty()) {
                tpsStatuser.addAll(tpsfService.checkAvailStatus(identerAaSjekke, request.getSyntetisk()));
            }
            i++;
        }

        return tpsStatuser;
    }

    private Set<String> genererIdenter(HentIdenterRequest request) {

        if (request.getIdenttype() == Identtype.FDAT) {
            return identGeneratorService.genererIdenterFdat(request, new HashSet<>());
        } else {
            return identGeneratorService.genererIdenter(request, new HashSet<>());
        }
    }
}
