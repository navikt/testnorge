package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.provider.rs.request.SyntetiserFrikortRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final int ANTALL_FRIKORT_PER_IDENT = 1;

    private final FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;
    private final KonverteringService konverteringService;
    private final MqService mqService;

    public List<String> opprettSyntetiskeFrikort(
            SyntetiserFrikortRequest syntetiserFrikortRequest,
            boolean leggPaaKoe
    ) throws JAXBException {
        List<String> identer = new ArrayList<>(); // fra hodejegeren

        var identMap = identer.stream().collect(Collectors.toMap(ident -> ident, ident -> ANTALL_FRIKORT_PER_IDENT, (a, b) -> b));

        var egenandeler = frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(identMap);

        var xmlMeldinger = konverteringService.konverterEgenandelerTilXmlString(egenandeler);

        log.info("{} egenandelsmelding(er) ble generert og gjort om til XMLString.", xmlMeldinger.size());

        if (leggPaaKoe) {
            mqService.leggTilMeldingerPaaKoe(xmlMeldinger);
            log.info("Generert(e) egenandelsmelding(er) ble lagt til på kø.");
        }

        return null;
    }
}
