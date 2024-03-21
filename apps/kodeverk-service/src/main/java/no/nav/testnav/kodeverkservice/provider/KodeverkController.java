package no.nav.testnav.kodeverkservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.kodeverkservice.domain.KodeverkAdjusted;
import no.nav.testnav.kodeverkservice.service.KodeverkService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/kodeverk")
@Validated
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @GetMapping("/kodeverk/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public Flux<KodeverkAdjusted> getKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {

        return kodeverkService.getKodeverkByName(kodeverkNavn);
    }

    @GetMapping("/kodeverk")
    @Operation(description = "Hent kodeverk, returnerer map")
    public Mono<Map<String, String>> fetchKodeverk(@RequestParam String kodeverk) {

        return kodeverkService.getKodeverkMap(kodeverk);
    }
}
