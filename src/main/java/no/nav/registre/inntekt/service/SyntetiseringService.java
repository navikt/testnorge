package no.nav.registre.inntekt.service;

import static no.nav.registre.inntekt.utils.DatoParser.hentMaanedsnavnFraMaanedsnummer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.registre.inntekt.consumer.rs.v2.InntektstubV2Consumer;
import no.nav.registre.inntekt.domain.IdentMedData;
import no.nav.registre.inntekt.domain.InntektSaveInHodejegerenRequest;
import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.RsInntektsinformasjonsType;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.utils.DatoParser;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final int MINIMUM_ALDER = 13;
    private static final String INNTEKT_NAME = "inntekt";
    private static final int PAGE_SIZE = 100;

    @Value("${andelNyeIdenter}")
    private int andelNyeIdenter;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final InntektSyntConsumer inntektSyntConsumer;
    private final InntektstubV2Consumer inntektstubV2Consumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    public Map<String, List<RsInntekt>> startSyntetisering(SyntetiseringsRequest syntetiseringsRequest) {
        Set<String> identer = new HashSet<>(hentLevendeIdenterOverAlder(syntetiseringsRequest.getAvspillergruppeId()));
        Set<String> identerIInntektstub = new HashSet<>(inntektstubV2Consumer.hentEksisterendeIdenter());

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

        Map<String, List<RsInntekt>> feiledeInntektsmeldinger = new HashMap<>();
        Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger = new HashMap<>();

        opprettInntekterPaaEksisterende(identerIInntektstub, feiledeInntektsmeldinger, syntetiskeInntektsmeldinger);

        SortedMap<String, List<RsInntekt>> nyeIdenterMedInntekt = new TreeMap<>();
        for (String ident : nyeIdenter) {
            nyeIdenterMedInntekt.put(ident, new ArrayList<>());
        }

        opprettInntekterPaaNye(feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, nyeIdenterMedInntekt);

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

    private void opprettInntekterPaaEksisterende(
            Set<String> identerIInntektstub,
            Map<String, List<RsInntekt>> feiledeInntektsmeldinger,
            Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger
    ) {
        List<List<String>> partisjonerteIdenterIInntektstub = paginerIdenter(new ArrayList<>(identerIInntektstub));
        for (int i = 0; i < partisjonerteIdenterIInntektstub.size(); i++) {
            SortedMap<String, List<RsInntekt>> identerMedInntekt = new TreeMap<>();
            Map<String, List<Inntektsinformasjon>> identerMedInntektsinformasjon = new HashMap<>();

            inntektstubV2Consumer.hentEksisterendeInntekterPaaIdenter(partisjonerteIdenterIInntektstub.get(i))
                    .forEach(inntektsinformasjon -> {
                        String ident = inntektsinformasjon.getNorskIdent();
                        if (identerMedInntektsinformasjon.containsKey(ident)) {
                            identerMedInntektsinformasjon.get(ident).add(inntektsinformasjon);
                        } else {
                            identerMedInntektsinformasjon.put(ident, new ArrayList<>(Collections.singletonList(inntektsinformasjon)));
                        }
                    });

            identerMedInntektsinformasjon.forEach((key, value) -> {
                List<RsInntekt> inntekter = mapInntektsinformasjonslisteTilRsInntektListe(value);
                inntekter = DatoParser.finnSenesteInntekter(inntekter);
                identerMedInntekt.put(key, inntekter);
            });

            SortedMap<String, List<RsInntekt>> inntektsmeldingerFraSynt = getInntektsmeldingerFraSynt(identerMedInntekt);

            if (!leggTilHvisGyldig(feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, inntektsmeldingerFraSynt)) {
                continue;
            }

            log.info("La til page {} av {} med inntekter til eksisterende identer i inntektstub", i + 1, partisjonerteIdenterIInntektstub.size());
        }
    }

    private void opprettInntekterPaaNye(
            Map<String, List<RsInntekt>> feiledeInntektsmeldinger,
            Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger,
            SortedMap<String, List<RsInntekt>> nyeIdenterMedInntekt
    ) {
        List<Map<String, List<RsInntekt>>> paginerteIdenterMedInntekt = paginerInntekter(nyeIdenterMedInntekt);
        for (int i = 0; i < paginerteIdenterMedInntekt.size(); i++) {
            SortedMap<String, List<RsInntekt>> inntektsmeldingerFraSynt = getInntektsmeldingerFraSynt(paginerteIdenterMedInntekt.get(i));

            if (!leggTilHvisGyldig(feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, inntektsmeldingerFraSynt)) {
                continue;
            }

            log.info("La til page {} av {} med inntekter til nye identer i inntektstub", i + 1, paginerteIdenterMedInntekt.size());
        }
    }

    private List<String> hentLevendeIdenterOverAlder(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER);
    }

    private SortedMap<String, List<RsInntekt>> getInntektsmeldingerFraSynt(Map<String, List<RsInntekt>> identerMedInntekt) {
        return inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(identerMedInntekt);
    }

    private boolean leggTilHvisGyldig(
            Map<String, List<RsInntekt>> feiledeInntektsmeldinger,
            Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger,
            SortedMap<String, List<RsInntekt>> inntektsmeldingerFraSynt
    ) {
        if (inntektsmeldingerFraSynt == null) {
            log.warn("Fikk ingen syntetiserte meldinger synt-pakken. Fortsetter med neste bolk");
            return false;
        }

        inntektsmeldingerFraSynt.keySet().retainAll(filtrerGyldigeMeldinger(inntektsmeldingerFraSynt).keySet());

        if (inntektsmeldingerFraSynt.isEmpty()) {
            return false;
        }

        inntektstubV2Consumer.leggInntekterIInntektstub(inntektsmeldingerFraSynt)
                .stream()
                .filter(inntekt -> inntekt.getFeilmelding() != null && !inntekt.getFeilmelding().isEmpty())
                .forEach(inntekt -> {
                    if (feiledeInntektsmeldinger.containsKey(inntekt.getNorskIdent())) {
                        feiledeInntektsmeldinger.get(inntekt.getNorskIdent()).addAll(mapInntektsinformasjonTilRsInntekter(inntekt));
                    } else {
                        feiledeInntektsmeldinger.put(inntekt.getNorskIdent(), mapInntektsinformasjonTilRsInntekter(inntekt));
                    }
                });

        syntetiskeInntektsmeldinger.putAll(inntektsmeldingerFraSynt);
        return true;
    }

    private Map<String, List<RsInntekt>> filtrerGyldigeMeldinger(Map<String, List<RsInntekt>> inntektsmeldinger) {
        inntektsmeldinger.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        if (inntektsmeldinger.isEmpty()) {
            log.info("Ingen syntetiserte meldinger å legge på inntektstub, returnerer");
        }
        return inntektsmeldinger;
    }

    private static List<RsInntekt> mapInntektsinformasjonslisteTilRsInntektListe(List<Inntektsinformasjon> inntektsinformasjonListe) {
        List<RsInntekt> inntekter = new ArrayList<>();
        inntektsinformasjonListe.stream().map(SyntetiseringService::mapInntektsinformasjonTilRsInntekter).forEach(inntekter::addAll);
        return inntekter;
    }

    private static List<RsInntekt> mapInntektsinformasjonTilRsInntekter(Inntektsinformasjon inntektsinformasjon) {
        return inntektsinformasjon.getInntektsliste().stream().map(inntekt -> RsInntekt.builder()
                .beloep(inntekt.getBeloep())
                .inntektstype(inntekt.getInntektstype().toString())
                .aar(String.valueOf(inntektsinformasjon.getAarMaaned().getYear()))
                .maaned(hentMaanedsnavnFraMaanedsnummer(inntektsinformasjon.getAarMaaned().getMonthValue()))
                .inntektsinformasjonsType(RsInntektsinformasjonsType.INNTEKT)
                .inngaarIGrunnlagForTrekk(inntekt.isInngaarIGrunnlagForTrekk())
                .utloeserArbeidsgiveravgift(inntekt.isUtloeserArbeidsgiveravgift())
                .virksomhet(inntektsinformasjon.getVirksomhet())
                .build()).collect(Collectors.toList());
    }

    private static List<Map<String, List<RsInntekt>>> paginerInntekter(SortedMap<String, List<RsInntekt>> map) {
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

    private static List<List<String>> paginerIdenter(List<String> list) {
        int size = list.size();
        int m = size / PAGE_SIZE;
        if (size % PAGE_SIZE != 0) {
            m++;
        }

        List<List<String>> partisjoner = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            int fromIndex = i * PAGE_SIZE;
            int toIndex = (i * PAGE_SIZE + PAGE_SIZE < size) ? (i * PAGE_SIZE + PAGE_SIZE) : size;

            partisjoner.add(new ArrayList<>(list.subList(fromIndex, toIndex)));
        }
        return partisjoner;
    }
}
