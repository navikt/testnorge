package no.nav.testnav.kodeverkservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.kodeverkservice.domain.KodeverkAdjusted;
import no.nav.testnav.kodeverkservice.service.KodeverkService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/kodeverk")
@Validated
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @GetMapping("/kodeverk/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public Mono<KodeverkAdjusted> getKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {

        var startTime = System.currentTimeMillis();
        log.info("Startet å hente kodeverk {}", kodeverkNavn);

        var response =  kodeverkService.getKodeverkByName(kodeverkNavn);

        log.info("Medgått tid til hente kodeverk {} er {} ms", kodeverkNavn, System.currentTimeMillis() - startTime);
        return response;
    }

    @GetMapping("/kodeverk")
    @Operation(description = "Hent kodeverk, returnerer map")
    public Mono<Map<String, String>> fetchKodeverk(@RequestParam String kodeverk) {

        var startTime = System.currentTimeMillis();
        log.info("Startet å hente kodeverk {}", kodeverk);

        var response = kodeverkService.getKodeverkMap(kodeverk);

        log.info("Medgått tid til hente kodeverk {} er {} ms", kodeverk, System.currentTimeMillis() - startTime);
        return response;
    }
}
