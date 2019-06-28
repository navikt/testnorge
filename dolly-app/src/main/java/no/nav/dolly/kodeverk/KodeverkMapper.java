package no.nav.dolly.kodeverk;

import static java.util.Objects.nonNull;

import no.nav.dolly.domain.resultset.kodeverk.KodeAdjusted;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.tjenester.kodeverk.api.v1.Betydning;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * Mapper fra Betydninger i Kodeverkapp til Kodeverkobjekter som er lett for frontend å bruke
 * Tar nå kun første Betydningen den finner for en kode(value) siden man henter gyldig så vil det i fleste tilfeller gi 1 beskrivelse (?)
 * Vurder om man skal ta en hensyn til flere Betydninger per kode etter hvert.
 */
@Service
public class KodeverkMapper {

    private static final String KODE_BOKMAAL = "nb";

    public KodeverkAdjusted mapBetydningToAdjustedKodeverk(String kodeverkNavn, Map<String, List<Betydning>> betydningerSortedByKoder) {
        KodeverkAdjusted kodeverkAdjusted = KodeverkAdjusted.builder().name(kodeverkNavn).koder(new ArrayList<>()).build();

        if (nonNull(betydningerSortedByKoder) && !betydningerSortedByKoder.isEmpty()) {
            kodeverkAdjusted.getKoder().addAll(extractKoderFromBetydninger(betydningerSortedByKoder));
        }

        kodeverkAdjusted.getKoder().sort((kode1, kode2) -> kode1.getLabel().compareToIgnoreCase(kode2.getLabel()));
        return kodeverkAdjusted;
    }

    private List<KodeAdjusted> extractKoderFromBetydninger(Map<String, List<Betydning>> kodeMap) {
        return kodeMap.entrySet().stream()
                .filter(e -> nonNull(e.getValue()) && !e.getValue().isEmpty())
                .map(e -> KodeAdjusted.builder()
                        .label(e.getValue().get(0).getBeskrivelser().get(KODE_BOKMAAL).getTerm())
                        .value(e.getKey())
                        .gyldigFra(e.getValue().get(0).getGyldigFra())
                        .gyldigTil(e.getValue().get(0).getGyldigTil())
                        .build())
                .collect(Collectors.toList());
    }
}