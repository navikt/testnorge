package no.nav.registre.frikort.service.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponseDTO;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.frikort.consumer.rs.SyntFrikortConsumer;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.domain.xml.Egenandelskode;
import no.nav.registre.frikort.exceptions.UgyldigSamhandlerdataException;
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
    private static final Map<String, Integer> SAMHANDLERTYPE_MAKS_BELOEP;
    private static final Long AVSPILLERGRUPPE_SAMHANDLERE = 100001163L;
    private static final String MILJOE_SAMHANDLERE = "q2";

    private final SyntFrikortConsumer syntFrikortConsumer;
    private final KonverteringService konverteringService;
    private final MqService mqService;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final Random rand;

    static {
        SAMHANDLERTYPE_MAKS_BELOEP = new HashMap<>();
        URL resourceSamhandlertyper = Resources.getResource("samhandlertypekode_til_maksbeloep.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var map = objectMapper.readValue(resourceSamhandlertyper, new TypeReference<Map<String, Integer>>() {
            });
            SAMHANDLERTYPE_MAKS_BELOEP.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn samhandlertyper.", e);
        }
    }

    public List<SyntetiserFrikortResponse> hentSyntetiskeEgenandelerOgLeggPaaKoe(
            Map<String, Integer> identMap,
            boolean leggPaaKoe,
            boolean validerEgenandeler
    ) throws JAXBException {
        var samhandlerePersondata = hentSamhandlere();
        var egenandeler = hentSyntetiskeEgenandelerPaginert(identMap, validerEgenandeler);
        return konverterTilXMLOgLeggPaaKoe(egenandeler, samhandlerePersondata, leggPaaKoe);
    }

    public List<SyntetiserFrikortResponse> konverterTilXMLOgLeggPaaKoe(
            Map<String, List<SyntFrikortResponseDTO>> egenandeler,
            List<PersondataResponse> samhandlerePersondata,
            boolean leggPaaKoe
    )
            throws JAXBException {
        var xmlMeldinger = konverteringService.konverterEgenandelerTilXmlString(egenandeler, samhandlerePersondata);

        var opprettedeEgenandeler = xmlMeldinger.stream().map(xmlMelding -> SyntetiserFrikortResponse.builder()
                .xml(xmlMelding)
                .lagtPaaKoe(LeggPaaKoeStatus.NO)
                .build()).collect(Collectors.toList());

        if (leggPaaKoe) {
            mqService.leggTilMeldingerPaaKoe(opprettedeEgenandeler);
        }

        return opprettedeEgenandeler;
    }

    public Map<String, List<SyntFrikortResponseDTO>> hentSyntetiskeEgenandelerPaginert(
            Map<String, Integer> identMap,
            boolean validerEgenandeler
    ) {
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
                    egenandeler.putAll(syntFrikortConsumer.hentSyntetiskeEgenandelerFraSyntRest(paginertMap));
                } catch (Exception e) {
                    log.error("Kunne ikke opprette syntetiske egenandeler på identer. Fortsetter med neste batch.", e);
                }
                paginertMap.clear();
                parsedSize = 0;
            }
        }

        return egenandeler.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(e -> !harUgyldigKombinasjon(e, validerEgenandeler))
                                .map(response -> new SyntFrikortResponseDTO(response, validerEgenandeler, rand))
                                .collect(Collectors.toList())
                ));
    }

    public static boolean harUgyldigKombinasjon(SyntFrikortResponse response, boolean validerEgenandeler) {
        if (!validerEgenandeler) {
            return false;
        }
        if (Egenandelskode.F == response.getEgenandelskode() && response.getEgenandelsats() <= 0) {
            log.warn("Ugyldig kombinasjon: egenandelskode 'F' og sats {}", response.getEgenandelsats());
            return true;
        }
        if (Egenandelskode.C == response.getEgenandelskode() && (response.getEgenandelsats() <= 0 || response.getEgenandelsbelop() <= 0)) {
            log.warn("Ugyldig kombinasjon: egenandelskode 'C' og sats {}  med beløp {}", response.getEgenandelsats(), response.getEgenandelsbelop());
            return true;
        }
        return false;
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
            throw new UgyldigSamhandlerdataException("Kunne ikke hente noen samhandlere");
        }
        return samhandlerePersondata;
    }
}
