package no.nav.registre.frikort.service.util;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.domain.xml.Egenandelskode;
import no.nav.registre.frikort.exception.UgyldigSamhandlerdataException;
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

    private final FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;
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
            Map<String, List<SyntFrikortResponse>> egenandeler,
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

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeEgenandelerPaginert(
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
                    egenandeler.putAll(frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(paginertMap));
                } catch (Exception e) {
                    log.error("Kunne ikke opprette syntetiske egenandeler på identer. Fortsetter med neste batch.", e);
                }
                paginertMap.clear();
                parsedSize = 0;
            }
        }
        if (validerEgenandeler) {
            var alleEgenandeler = new ArrayList<SyntFrikortResponse>();
            egenandeler.values().forEach(alleEgenandeler::addAll);
            validerEgenandeler(alleEgenandeler);
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
            throw new UgyldigSamhandlerdataException("Kunne ikke hente noen samhandlere");
        }
        return samhandlerePersondata;
    }

    private void validerEgenandeler(List<SyntFrikortResponse> egenandeler) {
        settGyldigeDatoer(egenandeler);
        settGyldigEgenandelskodeOgBeloep(egenandeler);
        settGyldigBeloepGittSamhandlertypekode(egenandeler);
    }

    private void settGyldigeDatoer(List<SyntFrikortResponse> egenandeler) {
        for (var egenandel : egenandeler) {
            var datoMottatt = egenandel.getDatoMottatt();
            var datoTjeneste = egenandel.getDatoTjeneste();
            if (ChronoUnit.WEEKS.between(datoMottatt, LocalDateTime.now()) > 12) {
                datoMottatt = LocalDateTime.now().minusWeeks(rand.nextInt(6)).minusWeeks(1);
                datoTjeneste = datoMottatt.minusDays(rand.nextInt(7));
            }
            egenandel.setDatoMottatt(datoMottatt);
            egenandel.setDatoTjeneste(datoTjeneste);
        }
    }

    private void settGyldigEgenandelskodeOgBeloep(List<SyntFrikortResponse> egenandeler) {
        var egenandelIterator = egenandeler.iterator();
        while (egenandelIterator.hasNext()) {
            var egenandel = egenandelIterator.next();
            if (egenandel.getEgenandelsbelop() == null) {
                egenandel.setEgenandelsbelop(egenandel.getEgenandelsats());
            }
            var egenandelskode = egenandel.getEgenandelskode();
            if (Egenandelskode.C != egenandelskode) {
                egenandel.setEgenandelsbelop(0.0);
            }
            if (Egenandelskode.F != egenandelskode && Egenandelskode.C != egenandelskode) {
                egenandel.setEgenandelsats(0.0);
            }
            if (Egenandelskode.F == egenandelskode && egenandel.getEgenandelsats() <= 0) {
                log.error("Ugyldig kombinasjon: egenandelskode 'F' og sats {}", egenandel.getEgenandelsats());
                egenandelIterator.remove();
            }
            if (Egenandelskode.C == egenandelskode && (egenandel.getEgenandelsats() <= 0 || egenandel.getEgenandelsbelop() <= 0)) {
                log.error("Ugyldig kombinasjon: egenandelskode 'C' og sats {}  med beløp {}", egenandel.getEgenandelsats(), egenandel.getEgenandelsbelop());
                egenandelIterator.remove();
            }
        }
    }

    private void settGyldigBeloepGittSamhandlertypekode(
            List<SyntFrikortResponse> egenandeler
    ) {
        for (var egenandel : egenandeler) {
            var maksBeloep = SAMHANDLERTYPE_MAKS_BELOEP.get(egenandel.getSamhandlertypekode().toString());
            if (egenandel.getEgenandelsbelop() > maksBeloep) {
                egenandel.setEgenandelsbelop(maksBeloep * 0.9);
            }
            if (egenandel.getEgenandelsats() > maksBeloep) {
                egenandel.setEgenandelsats(maksBeloep * 0.9);
            }
        }
    }
}
