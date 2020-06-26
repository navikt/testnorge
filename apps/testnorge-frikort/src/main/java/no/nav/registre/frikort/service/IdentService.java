package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.frikort.provider.rs.request.IdentMedAntallFrikort;
import no.nav.registre.frikort.provider.rs.request.IdentRequest;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.service.common.ServiceUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final ServiceUtils serviceUtils;

    public List<SyntetiserFrikortResponse> hentSyntetiskeEgenandelerSomXML(
            IdentRequest identRequest,
            boolean leggPaaKoe
    ) throws JAXBException {
        var identMap = identRequest.getIdenter().stream().collect(Collectors.toMap(IdentMedAntallFrikort::getIdent, IdentMedAntallFrikort::getAntallFrikort, (a, b) -> b));

        return serviceUtils.hentSyntetiskeFrikortOgLeggPaaKoe(identMap, leggPaaKoe);
    }
}
