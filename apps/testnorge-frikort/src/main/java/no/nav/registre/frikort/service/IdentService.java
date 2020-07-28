package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.provider.rs.request.EgenandelRequest;
import no.nav.registre.frikort.provider.rs.request.IdentMedAntallFrikort;
import no.nav.registre.frikort.provider.rs.request.IdentRequest;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.service.util.ServiceUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final ServiceUtils serviceUtils;

    public List<SyntetiserFrikortResponse> opprettSyntetiskeEgenandeler(
            IdentRequest identRequest,
            boolean leggPaaKoe,
            boolean validerEgenandeler
    ) throws JAXBException {
        var identMap = identRequest.getIdenter().stream().collect(Collectors.toMap(IdentMedAntallFrikort::getIdent, IdentMedAntallFrikort::getAntallFrikort, (a, b) -> b));

        return serviceUtils.hentSyntetiskeEgenandelerOgLeggPaaKoe(identMap, leggPaaKoe, validerEgenandeler);
    }

    public List<SyntetiserFrikortResponse> opprettEgenandeler(
            List<EgenandelRequest> egenandeler,
            boolean leggPaaKoe
    ) throws JAXBException {
        var samhandlerePersondata = serviceUtils.hentSamhandlere();
        var egenandelMap = egenandeler.stream().collect(Collectors.toMap(EgenandelRequest::getIdent, EgenandelRequest::getEgenandeler, (a, b) -> b));
        return serviceUtils.konverterTilXMLOgLeggPaaKoe(egenandelMap, samhandlerePersondata, leggPaaKoe);
    }

    public List<String> hentTilgjengeligeMiljoer() {
        return Collections.singletonList("q2");
    }

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeEgenandeler(int antallEgenandeler, boolean validerEgenandeler) {
        Map<String, Integer> identMap = new HashMap<>();
        for (int i = 0; i < antallEgenandeler; i++) {
            identMap.put("IDENT_" + (i + 1), 1);
        }
        return serviceUtils.hentSyntetiskeEgenandelerPaginert(identMap, validerEgenandeler);
    }
}
