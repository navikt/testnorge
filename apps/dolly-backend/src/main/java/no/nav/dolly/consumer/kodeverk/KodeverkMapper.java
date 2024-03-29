package no.nav.dolly.consumer.kodeverk;

import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse.Betydning;
import no.nav.dolly.domain.resultset.kodeverk.KodeAdjusted;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/***
 * Mapper fra Betydninger i Kodeverkapp til Kodeverkobjekter som er lett for frontend å bruke
 * Tar nå kun første Betydningen den finner for en kode(value) siden man henter gyldig så vil det i fleste tilfeller gi 1 beskrivelse (?)
 * Vurder om man skal ta en hensyn til flere Betydninger per kode etter hvert.
 */
@Component
public class KodeverkMapper {

    private static final String KODE_BOKMAAL = "nb";

    public Flux<KodeverkAdjusted> mapBetydningToAdjustedKodeverk(String kodeverkNavn,
                                                                 Flux<KodeverkBetydningerResponse> kodeverkBetydningerResponse) {

        return kodeverkBetydningerResponse
                .map(KodeverkBetydningerResponse::getBetydninger)
                .map(betydning -> KodeverkAdjusted.builder()
                        .name(kodeverkNavn)
                        .koder(extractKoderFromBetydninger(betydning))
                        .build())
                .cache(Duration.ofHours(9));
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
                .sorted((kode1, kode2) -> kode1.getLabel().compareToIgnoreCase(kode2.getLabel()))
                .toList();
    }
}