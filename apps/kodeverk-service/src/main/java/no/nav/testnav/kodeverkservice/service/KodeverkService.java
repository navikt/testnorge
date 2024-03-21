package no.nav.testnav.kodeverkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.kodeverkservice.consumer.KodeverkConsumer;
import no.nav.testnav.kodeverkservice.domain.KodeverkAdjusted;
import no.nav.testnav.kodeverkservice.domain.KodeverkBetydningerResponse;
import no.nav.testnav.kodeverkservice.mapping.KodeverkMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {

    private final KodeverkConsumer kodeverkConsumer;
    private final KodeverkMapper kodeverkMapper;

    public Flux<KodeverkAdjusted> getKodeverkByName(String kodeverk) {

        var response = kodeverkConsumer.getKodeverk(kodeverk);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverk, response);
    }

    public Mono<Map<String, String>> getKodeverkMap(String kodeverk) {

        return kodeverkConsumer.getKodeverk(kodeverk)
                .map(KodeverkBetydningerResponse::getBetydninger)
                .map(Map::entrySet)
                .flatMap(Flux::fromIterable)
                .filter(entry -> !entry.getValue().isEmpty())
                .filter(entry -> LocalDate.now().isAfter(entry.getValue().getFirst().getGyldigFra()) &&
                        LocalDate.now().isBefore(entry.getValue().getFirst().getGyldigTil()))
                .collect(Collectors.toMap(Map.Entry::getKey, KodeverkService::getNorskBokmaal))
                .cache(Duration.ofHours(9));
    }

    private static String getNorskBokmaal(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().getFirst().getBeskrivelser().get("nb").getTekst();
    }
}
