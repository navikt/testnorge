package no.nav.registre.inntekt.service;

import static no.nav.registre.inntekt.utils.CommonConstants.TYPE_ORGANISASJON;
import static no.nav.registre.inntekt.utils.CommonConstants.TYPE_PERSON;
import static no.nav.registre.inntekt.utils.DatoParser.finnSenesteInntekter;
import static no.nav.registre.inntekt.utils.DatoParser.hentMaanedsnavnFraMaanedsnummer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Organisasjon;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Person;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
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

import no.nav.registre.inntekt.consumer.rs.SyntInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
import no.nav.registre.inntekt.domain.IdentMedData;
import no.nav.registre.inntekt.domain.InntektSaveInHodejegerenRequest;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.registre.inntekt.domain.inntektstub.RsInntektsinformasjonsType;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

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
    private final SyntInntektConsumer inntektSyntConsumer;
    private final InntektstubV2Consumer inntektstubV2Consumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final AaregService aaregService;

    public Map<String, List<RsInntekt>> startSyntetisering(
            SyntetiseringsRequest syntetiseringsRequest,
            boolean opprettPaaEksisterende
    ) {
        var identer = new HashSet<>(hentLevendeIdenterOverAlder(syntetiseringsRequest.getAvspillergruppeId()));
        var identerIAareg = new HashSet<>(aaregService.hentIdenterMedArbeidsforhold(syntetiseringsRequest.getAvspillergruppeId(), syntetiseringsRequest.getMiljoe()));
        var identerIInntektstub = new HashSet<>(inntektstubV2Consumer.hentEksisterendeIdenter());

        identerIInntektstub.retainAll(identerIAareg);

        int antallNyeIdenterMedInntekt = identer.size() / andelNyeIdenter - identerIInntektstub.size();
        int antallIdenterUtenArbeidsforhold = identer.size() / andelNyeIdenter - identerIAareg.size();

        log.info("{} identer mangler inntekt", antallNyeIdenterMedInntekt);
        log.info("{} identer mangler arbeidsforhold", antallIdenterUtenArbeidsforhold);

        identerIAareg.removeAll(identerIInntektstub);
        List<String> nyeIdenter;
        if (antallNyeIdenterMedInntekt <= 0) {
            log.info("Tilstrekkelig mange identer i mininorge har allerede inntekt. Oppretter ikke inntekt på nye identer.");
            nyeIdenter = Collections.emptyList();
        } else {
            antallNyeIdenterMedInntekt = Math.min(antallNyeIdenterMedInntekt, identerIAareg.size());
            nyeIdenter = new ArrayList<>(identerIAareg).subList(0, antallNyeIdenterMedInntekt);
        }

        if (identerIInntektstub.isEmpty() && nyeIdenter.isEmpty()) {
            log.warn("Ingen identer å opprette meldinger på");
            return null;
        }

        Map<String, List<RsInntekt>> feiledeInntektsmeldinger = new HashMap<>();
        Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger = new HashMap<>();

        if (opprettPaaEksisterende) {
            opprettInntekterPaaEksisterende(identerIInntektstub, feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, syntetiseringsRequest.getMiljoe());
        }

        SortedMap<String, List<RsInntekt>> nyeIdenterMedInntekt = new TreeMap<>();
        for (var ident : nyeIdenter) {
            nyeIdenterMedInntekt.put(ident, new ArrayList<>());
        }

        opprettInntekterPaaNye(feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, nyeIdenterMedInntekt, syntetiseringsRequest.getMiljoe());

        if (!feiledeInntektsmeldinger.isEmpty()) {
            log.warn("Kunne ikke opprette inntekt på følgende identer: {}", feiledeInntektsmeldinger.keySet());

            feiledeInntektsmeldinger.keySet().forEach(syntetiskeInntektsmeldinger::remove);
        }

        List<IdentMedData> identerMedData = new ArrayList<>(syntetiskeInntektsmeldinger.size());
        syntetiskeInntektsmeldinger.forEach((ident, inntekter) -> identerMedData.add(new IdentMedData(ident, inntekter)));

        hodejegerenHistorikkConsumer.saveHistory(new InntektSaveInHodejegerenRequest(INNTEKT_NAME, identerMedData));

        return feiledeInntektsmeldinger;
    }

    private void opprettInntekterPaaEksisterende(
            Set<String> identerIInntektstub,
            Map<String, List<RsInntekt>> feiledeInntektsmeldinger,
            Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger,
            String miljoe
    ) {
        var partisjonerteIdenterIInntektstub = paginerIdenter(new ArrayList<>(identerIInntektstub));
        for (int i = 0; i < partisjonerteIdenterIInntektstub.size(); i++) {
            SortedMap<String, List<RsInntekt>> identerMedInntekt = new TreeMap<>();
            Map<String, List<Inntektsinformasjon>> identerMedInntektsinformasjon = new HashMap<>();

            inntektstubV2Consumer.hentEksisterendeInntekterPaaIdenter(partisjonerteIdenterIInntektstub.get(i))
                    .forEach(inntektsinformasjon -> {
                        var ident = inntektsinformasjon.getNorskIdent();
                        if (identerMedInntektsinformasjon.containsKey(ident)) {
                            identerMedInntektsinformasjon.get(ident).add(inntektsinformasjon);
                        } else {
                            identerMedInntektsinformasjon.put(ident, new ArrayList<>(Collections.singletonList(inntektsinformasjon)));
                        }
                    });

            identerMedInntektsinformasjon.forEach((ident, inntektsinformasjon) -> {
                var inntekter = mapInntektsinformasjonslisteTilRsInntektListe(inntektsinformasjon);
                inntekter = finnSenesteInntekter(inntekter);
                identerMedInntekt.put(ident, inntekter);
            });

            var inntektsmeldingerFraSynt = getInntektsmeldingerFraSynt(identerMedInntekt, miljoe);

            if (!leggTilHvisGyldig(feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, inntektsmeldingerFraSynt)) {
                continue;
            }

            log.info("La til page {} av {} med inntekter til eksisterende identer i inntektstub", i + 1, partisjonerteIdenterIInntektstub.size());
        }
    }

    private void opprettInntekterPaaNye(
            Map<String, List<RsInntekt>> feiledeInntektsmeldinger,
            Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger,
            SortedMap<String, List<RsInntekt>> nyeIdenterMedInntekt,
            String miljoe
    ) {
        var paginerteIdenterMedInntekt = paginerInntekter(nyeIdenterMedInntekt);
        for (int i = 0; i < paginerteIdenterMedInntekt.size(); i++) {
            var inntektsmeldingerFraSynt = getInntektsmeldingerFraSynt(paginerteIdenterMedInntekt.get(i), miljoe);

            if (!leggTilHvisGyldig(feiledeInntektsmeldinger, syntetiskeInntektsmeldinger, inntektsmeldingerFraSynt)) {
                continue;
            }

            log.info("La til page {} av {} med inntekter til nye identer i inntektstub", i + 1, paginerteIdenterMedInntekt.size());
        }
    }

    private List<String> hentLevendeIdenterOverAlder(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER);
    }

    private SortedMap<String, List<RsInntekt>> getInntektsmeldingerFraSynt(
            Map<String, List<RsInntekt>> identerMedInntekt,
            String miljoe
    ) {
        var inntektsmeldingerFraSynt = inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(identerMedInntekt);
        inntektsmeldingerFraSynt.forEach((ident, inntekter) -> {
            for (var inntekt : inntekter) {
                var opplysningspliktig = getOpplysningspliktigId(ident, miljoe);
                if (opplysningspliktig == null) {
                    log.warn("Fant ikke opplysningspliktig på arbeidsforhold til ident {}", ident);
                }
                inntekt.setOpplysningspliktig(opplysningspliktig);
            }
        });
        return inntektsmeldingerFraSynt;
    }

    private boolean leggTilHvisGyldig(
            Map<String, List<RsInntekt>> feiledeInntektsmeldinger,
            Map<String, List<RsInntekt>> syntetiskeInntektsmeldinger,
            SortedMap<String, List<RsInntekt>> inntektsmeldingerFraSynt
    ) {
        if (inntektsmeldingerFraSynt == null) {
            log.warn("Fikk ingen syntetiserte meldinger fra synt-pakken. Fortsetter med neste bolk");
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

    private String getOpplysningspliktigId(
            String ident,
            String miljoe
    ) {
        var arbeidsforhold = aaregService.hentArbeidsforhold(ident, miljoe);
        if (arbeidsforhold == null || arbeidsforhold.isEmpty()) {
            return null;
        }

        var arbeidsforholdet = AaregService.finnNyesteArbeidsforhold(arbeidsforhold);
        if (arbeidsforholdet == null) {
            throw new RuntimeException("Fant ikke arbeidsforhold til ident " + ident);
        }

        var opplysningspliktig = arbeidsforholdet.getOpplysningspliktig();
        if (opplysningspliktig == null) {
            throw new RuntimeException("Fant ingen opplysningspliktig i arbeidsforholdet til ident " + ident);
        }

        var type = opplysningspliktig.getType();
        if (TYPE_ORGANISASJON.equals(type)) {
            return ((Organisasjon) opplysningspliktig).getOrganisasjonsnummer();
        } else if (TYPE_PERSON.equals(type)) {
            return ((Person) opplysningspliktig).getOffentligIdent();
        }
        return null;
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
            int toIndex = Math.min(i * PAGE_SIZE + PAGE_SIZE, size);

            partisjoner.add(new ArrayList<>(list.subList(fromIndex, toIndex)));
        }
        return partisjoner;
    }
}
