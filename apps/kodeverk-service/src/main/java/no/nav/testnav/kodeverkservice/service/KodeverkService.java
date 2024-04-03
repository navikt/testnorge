package no.nav.testnav.kodeverkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.kodeverkservice.consumer.KodeverkConsumer;
import no.nav.testnav.kodeverkservice.dto.KodeverkBetydningerResponse;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO.KodeAdjusted;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
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

    public Mono<KodeverkAdjustedDTO> getKodeverkByName(String kodeverknavn) {

        return kodeverkConsumer.getKodeverk(kodeverknavn)
                .map(kodeverk -> kodeverk.getBetydninger().entrySet())
                .map(betydninger -> KodeverkAdjustedDTO.builder()
                        .name(kodeverknavn)
                        .koder(betydninger.stream()
                                .filter(entry -> nonNull(entry.getValue()) && !entry.getValue().isEmpty())
                                .map(KodeverkService::getKodeAdjusted)
                                .sorted(Comparator.comparing(KodeverkAdjustedDTO.KodeAdjusted::getLabel))
                                .toList())
                        .build());
    }

    public Mono<KodeverkDTO> getKodeverkMap(String kodeverknavn) {

        return kodeverkConsumer.getKodeverk(kodeverknavn)
                .map(kodeverk -> kodeverk.getBetydninger().entrySet().stream()
                        .filter(entry -> nonNull(entry.getValue()) && !entry.getValue().isEmpty())
                        .filter(entry -> LocalDate.now().isAfter(getBetydning(entry).getGyldigFra()) &&
                                LocalDate.now().isBefore(getBetydning(entry).getGyldigTil()))
                        .collect(Collectors.toMap(Map.Entry::getKey, KodeverkService::getNorskBokmaal)))
                .map(resultat -> KodeverkDTO.builder()
                        .kodeverknavn(kodeverknavn)
                        .kodeverk(resultat)
                        .build());
    }

    private static KodeAdjusted getKodeAdjusted(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return KodeAdjusted.builder()
                .label(getNorskBokmaal(entry))
                .gyldigFra(getBetydning(entry).getGyldigFra())
                .gyldigTil(getBetydning(entry).getGyldigTil())
                .value(entry.getKey())
                .build();
    }

    private static KodeverkBetydningerResponse.Betydning getBetydning(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().getFirst();
    }

    private static String getNorskBokmaal(Map.Entry<String, List<KodeverkBetydningerResponse.Betydning>> entry) {

        return getBetydning(entry).getBeskrivelser().get("nb").getTerm();
    }
}
