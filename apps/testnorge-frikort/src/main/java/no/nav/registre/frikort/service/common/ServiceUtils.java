package no.nav.registre.frikort.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.provider.rs.response.LeggPaaKoeStatus;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.service.KonverteringService;
import no.nav.registre.frikort.service.MqService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUtils {

    private final FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;
    private final KonverteringService konverteringService;
    private final MqService mqService;

    public List<SyntetiserFrikortResponse> hentSyntetiskeFrikortOgLeggPaaKoe(Map<String, Integer> identMap, boolean leggPaaKoe) throws JAXBException {
        var egenandeler = frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(identMap);

        var xmlMeldinger = konverteringService.konverterEgenandelerTilXmlString(egenandeler);

        log.info("{} egenandelsmelding(er) ble generert og gjort om til XMLString.", xmlMeldinger.size());

        var opprettedeFrikort = xmlMeldinger.stream().map(xmlMelding -> SyntetiserFrikortResponse.builder()
                .xml(xmlMelding)
                .lagtPaaKoe(LeggPaaKoeStatus.NO)
                .build()).collect(Collectors.toList());

        if (leggPaaKoe) {
            mqService.leggTilMeldingerPaaKoe(opprettedeFrikort);
        }

        return opprettedeFrikort;
    }
}
