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
import java.util.SortedMap;
import java.util.TreeMap;
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
    private static final int PAGE_SIZE = 100;

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

        SortedMap<String, List<RsInntekt>> identerMedInntekt = new TreeMap<>();
        for (String ident : identerIInntektstub) {
            List<RsInntekt> inntekter = inntektstubConsumer.hentEksisterendeInntekterPaaIdent(ident);
            inntekter = DatoParser.finnSenesteInntekter(inntekter);
            identerMedInntekt.put(ident, inntekter);
        }
        for (String ident : nyeIdenter) {
            identerMedInntekt.put(ident, new ArrayList<>());
        }

        List<Map<String, List<RsInntekt>>> paginerteIdenterMedInntekt = paginer(identerMedInntekt);
        Map<String, List<RsInntekt>> feiledeInntektsmeldinger = new HashMap<>();
        Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger = new HashMap<>();

        for (int i = 0; i < paginerteIdenterMedInntekt.size(); i++) {
            SortedMap<String, List<RsInntekt>> inntektsmeldingerFraSynt = getInntektsmeldingerFraSynt(paginerteIdenterMedInntekt.get(i));

            if (inntektsmeldingerFraSynt == null) {
                log.warn("Fikk ingen syntetiserte meldinger synt-pakken. Fortsetter med neste bolk");
                continue;
            }

            inntektsmeldingerFraSynt.keySet().retainAll(filtrerGyldigeMeldinger(inntektsmeldingerFraSynt).keySet());

            if (inntektsmeldingerFraSynt.isEmpty()) {
                continue;
            }

            feiledeInntektsmeldinger.putAll(inntektstubConsumer.leggInntekterIInntektstub(inntektsmeldingerFraSynt));
            syntetiskeInntektsmeldinger.putAll(inntektsmeldingerFraSynt);

            log.info("La til page {} av {} med inntekter i inntektstub", i + 1, paginerteIdenterMedInntekt.size());
        }

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

    private SortedMap<String, List<RsInntekt>> getInntektsmeldingerFraSynt(Map<String, List<RsInntekt>> identerMedInntekt) {
        return inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(identerMedInntekt);
    }

    private Map<String, List<RsInntekt>> filtrerGyldigeMeldinger(Map<String, List<RsInntekt>> inntektsmeldinger) {
        inntektsmeldinger.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        if (inntektsmeldinger.isEmpty()) {
            log.info("Ingen syntetiserte meldinger å legge på inntektstub, returnerer");
        }
        return inntektsmeldinger;
    }

    private static List<Map<String, List<RsInntekt>>> paginer(SortedMap<String, List<RsInntekt>> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        List<Map<String, List<RsInntekt>>> pages = new ArrayList<>();
        final int listSize = map.size();
        for (int i = 0; i < listSize; i += PAGE_SIZE) {
            if (i + PAGE_SIZE < listSize) {
                pages.add(map.subMap(keys.get(i), keys.get(i + PAGE_SIZE)));
            } else {
                pages.add(map.tailMap(keys.get(i)));
            }
        }
        return pages;
    }
}
