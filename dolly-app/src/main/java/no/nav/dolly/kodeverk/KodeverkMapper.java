package no.nav.dolly.kodeverk;

import no.nav.dolly.domain.resultset.kodeverk.KodeAdjusted;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.tjenester.kodeverk.api.v1.Betydning;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

/***
 * Mapper fra Betydninger i Kodeverkapp til Kodeverkobjekter som er lett for frontend å bruke
 * Tar nå kun første Betydningen den finner for en kode(value) siden man henter gyldig så vil det i fleste tilfeller gi 1 beskrivelse (?)
 * Vurder om man skal ta en hensyn til flere Betydninger per kode etter hvert.
 */
@Service
public class KodeverkMapper {

    private static final String KODE_BOKMAAL = "nb";

    public KodeverkAdjusted mapBetydningToAdjustedKodeverk(String kodeverkNavn, Optional<Map<String, List<Betydning>>> betydningerMap) {
        KodeverkAdjusted kodeverkAdjusted = betydningerMap.map(this::extractKodeverkFromBetydninger).orElse(new KodeverkAdjusted());
        kodeverkAdjusted.setName(kodeverkNavn);
        return kodeverkAdjusted;
    }

    private KodeverkAdjusted extractKodeverkFromBetydninger(Map<String, List<Betydning>> kodeMap) {
        List<KodeAdjusted> koder = kodeMap.entrySet().stream()
                .filter(e -> !isNullOrEmpty(e.getValue()))
                .map(e -> KodeAdjusted.builder()
                        .label(e.getKey() + " - " + e.getValue().get(0).getBeskrivelser().get(KODE_BOKMAAL).getTerm())
                        .value(e.getKey())
                        .build())
                .collect(Collectors.toList());

        return KodeverkAdjusted.builder().koder(koder).build();
    }
}
