package no.nav.testnav.kodeverkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.kodeverkservice.consumer.KodeverkConsumer;
import no.nav.testnav.kodeverkservice.domain.KodeverkAdjusted;
import no.nav.testnav.kodeverkservice.domain.KodeverkBetydningerResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {

    private final KodeverkConsumer kodeverkConsumer;

    public Mono<KodeverkAdjusted> getKodeverkByName(String kodeverk) {

        return kodeverkConsumer.getKodeverk(kodeverk)
                .map(KodeverkBetydningerResponse::getBetydninger)
                .map(Map::entrySet)
                .map(betydninger -> KodeverkAdjusted.builder()
                        .name(kodeverk)
                        .koder(betydninger.stream()
                                .filter(entry -> nonNull(entry.getValue()) && !entry.getValue().isEmpty())
                                .map(KodeverkService::getKodeAdjusted)
                                .sorted(Comparator.comparing(KodeverkAdjusted.KodeAdjusted::getLabel))
                                .toList())
                        .build());
    }

    public Mono<Map<String, String>> getKodeverkMap(String kodeverk) {

        return kodeverkConsumer.getKodeverk(kodeverk)
                .map(KodeverkBetydningerResponse::getBetydninger)
                .map(Map::entrySet)
                .map(betydninger -> betydninger.stream()
                        .filter(entry -> !entry.getValue().isEmpty())
                        .filter(entry -> LocalDate.now().isAfter(getBetydning(entry).getGyldigFra()) &&
                                LocalDate.now().isBefore(getBetydning(entry).getGyldigTil()))
                        .collect(Collectors.toMap(Map.Entry::getKey, KodeverkService::getNorskBokmaal)));
    }

    private static KodeverkAdjusted.KodeAdjusted getKodeAdjusted(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return KodeverkAdjusted.KodeAdjusted.builder()
                .label(entry.getKey())
                .gyldigFra(getBetydning(entry).getGyldigFra())
                .gyldigTil(getBetydning(entry).getGyldigTil())
                .value(getNorskBokmaal(entry))
                .build();
    }

    private static KodeverkBetydningerResponse.Betydning getBetydning(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().getFirst();
    }

    private static String getNorskBokmaal(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return getBetydning(entry).getBeskrivelser().get("nb").getTekst();
    }
}
