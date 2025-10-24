package no.nav.testnav.kodeverkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.kodeverkservice.utility.GamleKommunerUtility;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkMedHistoriskeService {

    private final KodeverkService kodeverkService;

    public Mono<KodeverkDTO> getKommunerMedHistoriske() {

        return kodeverkService.getKodeverkMap("Kommuner")
                .map(KodeverkDTO::getKodeverk)
                .flatMapMany(koder -> Flux.concat(Flux.fromIterable(koder.entrySet())
                                // Ekskluder kommune 1507 som er historisk, men er erstattet av kommunenr 1508
                                .filter(entry -> !entry.getKey().equals("1507")),
                        Flux.fromIterable(GamleKommunerUtility.getHistoriskeKommuner().entrySet())))
                .sort(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .map(koder -> KodeverkDTO.builder()
                        .kodeverknavn("KommunerMedHistoriske")
                        .kodeverk(koder)
                        .build());
    }

    public Mono<KodeverkAdjustedDTO> getKommunerMedHistoriskeAdjusted() {

        return kodeverkService.getKodeverkByName("Kommuner")
                .map(KodeverkAdjustedDTO::getKoder)
                .flatMapMany(koder -> Flux.concat(Flux.fromIterable(koder)
                                // Ekskluder kommune 1507 som er historisk, men er erstattet av kommunenr 1508
                                .filter(entry -> !entry.getValue().equals("1507")),
                        Flux.fromIterable(GamleKommunerUtility.getHistoriskeKommunerAdjusted())))
                .collectList()
                .map(koder -> KodeverkAdjustedDTO.builder()
                        .name("KommunerMedHistoriske")
                        .koder(koder)
                        .build());
    }
}
