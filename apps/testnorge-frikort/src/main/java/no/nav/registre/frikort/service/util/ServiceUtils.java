package no.nav.registre.frikort.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse.LeggPaaKoeStatus;
import no.nav.registre.frikort.service.KonverteringService;
import no.nav.registre.frikort.service.MqService;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.PersondataResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUtils {

    private static final int PAGE_SIZE = 50;

    private final FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;
    private final KonverteringService konverteringService;
    private final MqService mqService;
    private final HodejegerenConsumer hodejegerenConsumer;

    private static final Long AVSPILLERGRUPPE_SAMHANDLERE = 100001163L;
    private static final String MILJOE_SAMHANDLERE = "q2";

    public List<SyntetiserFrikortResponse> hentSyntetiskeEgenandelerOgLeggPaaKoe(
            Map<String, Integer> identMap,
            boolean leggPaaKoe,
            boolean validerEgenandel
    ) throws JAXBException {
        var samhandlerePersondata = hentSamhandlere();
        var egenandeler = hentSyntetiskeEgenandelerPaginert(identMap);
        return konverterTilXMLOgLeggPaaKoe(egenandeler, samhandlerePersondata, leggPaaKoe, validerEgenandel);
    }

    public List<SyntetiserFrikortResponse> konverterTilXMLOgLeggPaaKoe(
            Map<String, List<SyntFrikortResponse>> egenandeler,
            List<PersondataResponse> samhandlerePersondata,
            boolean leggPaaKoe,
            boolean validerEgenandel
    )
            throws JAXBException {
        var xmlMeldinger = konverteringService.konverterEgenandelerTilXmlString(egenandeler, samhandlerePersondata, validerEgenandel);

        var opprettedeEgenandeler = xmlMeldinger.stream().map(xmlMelding -> SyntetiserFrikortResponse.builder()
                .xml(xmlMelding)
                .lagtPaaKoe(LeggPaaKoeStatus.NO)
                .build()).collect(Collectors.toList());

        if (leggPaaKoe) {
            mqService.leggTilMeldingerPaaKoe(opprettedeEgenandeler);
        }

        return opprettedeEgenandeler;
    }

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeEgenandelerPaginert(Map<String, Integer> identMap) {
        var egenandeler = new HashMap<String, List<SyntFrikortResponse>>();
        var paginertMap = new HashMap<String, Integer>();
        int parsedSize = 0;
        int counter = 0;
        for (var entry : identMap.entrySet()) {
            parsedSize++;
            counter++;
            paginertMap.put(entry.getKey(), entry.getValue());
            if (parsedSize == PAGE_SIZE || counter == identMap.size()) {
                try {
                    egenandeler.putAll(frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(paginertMap));
                } catch (Exception e) {
                    log.error("Kunne ikke opprette syntetiske egenandeler på identer. Fortsetter med neste batch.", e);
                }
                paginertMap.clear();
                parsedSize = 0;
            }
        }
        return egenandeler;
    }

    public List<PersondataResponse> hentSamhandlere() {
        var samhandlere = hodejegerenConsumer.get(AVSPILLERGRUPPE_SAMHANDLERE);
        var samhandlerePersondata = new ArrayList<PersondataResponse>();
        for (var samhandler : samhandlere) {
            try {
                samhandlerePersondata.add(hodejegerenConsumer.getPersondata(samhandler, MILJOE_SAMHANDLERE));
                if (samhandlerePersondata.size() == 10) {
                    break;
                }
            } catch (RuntimeException e) {
                log.error("Kunne ikke hente samhandler", e);
            }
        }

        if (samhandlerePersondata.isEmpty()) {
            throw new RuntimeException("Kunne ikke hente noen samhandlere");
        }
        return samhandlerePersondata;
    }
}
