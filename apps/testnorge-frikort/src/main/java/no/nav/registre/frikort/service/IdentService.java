package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.provider.rs.request.IdentMedAntallFrikort;
import no.nav.registre.frikort.provider.rs.request.IdentRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;
    private final KonverteringService konverteringService;
    private final MqService mqService;

    public List<String> hentSyntetiskeEgenandelerSomXML(
            IdentRequest identRequest,
            boolean leggPaaKoe
    ) throws JAXBException {
        var identMap = identRequest.getIdenter().stream().collect(Collectors.toMap(IdentMedAntallFrikort::getIdent, IdentMedAntallFrikort::getAntallFrikort, (a, b) -> b));
        var egenandeler = frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(identMap);

        var xmlMeldinger = konverteringService.konverterEgenandelerTilXmlString(egenandeler);

        log.info("{} egenandelsmelding(er) ble generert og gjort om til XMLString.", xmlMeldinger.size());

        if (leggPaaKoe) {
            mqService.leggTilMeldingerPaaKoe(xmlMeldinger);
            log.info("Generert(e) egenandelsmelding(er) ble lagt til på kø.");
        }

        return xmlMeldinger;
    }
}
