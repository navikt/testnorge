package no.nav.identpool.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.identpool.consumers.TpsMessagingConsumer;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.identpool.repository.IdentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class IdenterAvailService {

    private static final int MAX_TPS_CALL_ATTEMPTS = 1;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final MapperFacade mapperFacade;

    public Set<TpsStatusDTO> generateAndCheckIdenter(HentIdenterRequest request, int antall) {

        HentIdenterRequest oppdatertRequest = mapperFacade.map(request, HentIdenterRequest.class);
        oppdatertRequest.setAntall(antall);
        Set<TpsStatusDTO> tpsStatuserDTO = new HashSet<>();
        int i = 0;

        while (i < MAX_TPS_CALL_ATTEMPTS &&
                tpsStatuserDTO.stream().filter(status -> !status.isInUse()).count() < request.getAntall()) {

            Set<String> genererteIdenter = genererIdenter(oppdatertRequest);
            Set<Ident> identerFinnesIdb = identRepository.findByPersonidentifikatorIn(genererteIdenter);

            Set<String> identerAaSjekke = genererteIdenter.stream()
                    .filter(ident -> identerFinnesIdb.stream()
                            .noneMatch(dbIdent -> dbIdent.getPersonidentifikator().equals(ident)))
                    .collect(Collectors.toSet());

            if (!identerAaSjekke.isEmpty()) {
                tpsStatuserDTO.addAll(isTrue(request.getSyntetisk()) ?
                        identerAaSjekke.stream()
                                .map(ident -> TpsStatusDTO.builder()
                                        .ident(ident)
                                        .inUse(false)
                                        .build())
                                .collect(Collectors.toSet())
                        :
                        tpsMessagingConsumer.getIdenterStatuser(identerAaSjekke));
            }
            i++;
        }

        return tpsStatuserDTO;
    }

    private Set<String> genererIdenter(HentIdenterRequest request) {

        if (request.getIdenttype() == Identtype.FDAT) {
            return identGeneratorService.genererIdenterFdat(request, new HashSet<>());
        } else {
            return identGeneratorService.genererIdenter(request, new HashSet<>());
        }
    }
}
