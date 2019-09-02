package no.nav.registre.inntekt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektSyntConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubConsumer;
import no.nav.registre.inntekt.domain.IdentMedData;
import no.nav.registre.inntekt.domain.InntektSaveInHodejegerenRequest;
import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.RsPerson;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.utils.DatoParser;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Service
public class SyntetiseringService {

    private static final int MINIMUM_ALDER = 13;
    private static final String INNTEKT_NAME = "inntekt";

    @Value("${andelNyeIdenter}")
    private int andelNyeIdenter;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private InntektSyntConsumer inntektSyntConsumer;

    @Autowired
    private InntektstubConsumer inntektstubConsumer;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    public Map<String, List<RsInntekt>> startSyntetisering(SyntetiseringsRequest syntetiseringsRequest) {

        Set<String> identer = new HashSet<>(hentLevendeIdenterOverAlder(syntetiseringsRequest.getAvspillergruppeId(), MINIMUM_ALDER));
        Set<String> identerIInntektstub = inntektstubConsumer.hentEksisterendeIdenter().stream().map(RsPerson::getFoedselsnummer).collect(Collectors.toSet());

        identerIInntektstub.retainAll(identer);

        int antallNyeIdenterMedInntekt = (identer.size() / andelNyeIdenter) - identerIInntektstub.size();

        identer.removeAll(identerIInntektstub);
        List<String> nyeIdenter;
        if (antallNyeIdenterMedInntekt <= 0) {
            log.info("Tilstrekkelig mange identer i mininorge har allerede inntekt. Oppretter ikke inntekt på nye identer.");
            nyeIdenter = Collections.emptyList();
        } else {
            nyeIdenter = new ArrayList<>(identer).subList(0, antallNyeIdenterMedInntekt);
        }

        if (identerIInntektstub.isEmpty() && nyeIdenter.isEmpty()) {
            log.warn("Ingen identer å opprette meldinger på");
            return null;
        }

        Map<String, List<RsInntekt>> identerMedInntekt = new HashMap<>();
        for (String ident : identerIInntektstub) {
            List<RsInntekt> inntekter = inntektstubConsumer.hentEksisterendeInntekterPaaIdent(ident);
            inntekter = DatoParser.finnSenesteInntekter(inntekter);
            identerMedInntekt.put(ident, inntekter);
        }
        for (String ident : nyeIdenter) {
            identerMedInntekt.put(ident, new ArrayList<>());
        }

        Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger = getInntektsmeldingerFraSynt(identerMedInntekt);
        if (syntetiskeInntektsmeldinger == null) {
            return new HashMap<>();
        }

        syntetiskeInntektsmeldinger.keySet().retainAll(filtrerGyldigeMeldinger(syntetiskeInntektsmeldinger).keySet());

        if (syntetiskeInntektsmeldinger.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<RsInntekt>> feiledeInntektsmeldinger = inntektstubConsumer.leggInntekterIInntektstub(syntetiskeInntektsmeldinger);

        if (!feiledeInntektsmeldinger.isEmpty()) {
            log.warn("Kunne ikke opprette inntekt på følgende identer: {}", feiledeInntektsmeldinger.keySet());

            for (Map.Entry<String, List<RsInntekt>> feilet : feiledeInntektsmeldinger.entrySet()) {
                syntetiskeInntektsmeldinger.remove(feilet.getKey());
            }
        }

        List<IdentMedData> identerMedData = new ArrayList<>(syntetiskeInntektsmeldinger.size());
        for (Map.Entry<String, List<RsInntekt>> personInfo : syntetiskeInntektsmeldinger.entrySet()) {
            identerMedData.add(new IdentMedData(personInfo.getKey(), personInfo.getValue()));
        }

        InntektSaveInHodejegerenRequest hodejegerenRequests = new InntektSaveInHodejegerenRequest(INNTEKT_NAME, identerMedData);
        List<String> lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequests);

        if (lagredeIdenter.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (IdentMedData ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(lagredeIdenter);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }

        return feiledeInntektsmeldinger;
    }

    private List<String> hentLevendeIdenterOverAlder(Long avspillergruppeId, int minimumAlder) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder);
    }

    private Map<String, List<RsInntekt>> getInntektsmeldingerFraSynt(Map<String, List<RsInntekt>> identerMedInntekt) {
        return inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(identerMedInntekt);
    }

    private Map<String, List<RsInntekt>> filtrerGyldigeMeldinger(Map<String, List<RsInntekt>> inntektsmeldinger) {
        inntektsmeldinger.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        if (inntektsmeldinger.isEmpty()) {
            log.info("Ingen syntetiserte meldinger å legge på inntektstub, returnerer");
        }
        return inntektsmeldinger;
    }
}
