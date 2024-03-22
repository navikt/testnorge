package no.nav.testnav.kodeverkservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.kodeverkservice.domain.KodeverkAdjusted;
import no.nav.testnav.kodeverkservice.service.KodeverkService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

import static no.nav.testnav.kodeverkservice.config.CacheConfig.CACHE_KODEVERK;
import static no.nav.testnav.kodeverkservice.config.CacheConfig.CACHE_KODEVERK_2;

@RestController
@RequestMapping("/api/v1/kodeverk")
@Validated
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @Cacheable(value = CACHE_KODEVERK, unless = "#result.size() == 0")
    @GetMapping
    @Operation(description = "Hent kodeverk, returnerer map")
    public Mono<Map<String, String>> fetchKodeverk(@RequestParam String kodeverk) {

        return kodeverkService.getKodeverkMap(kodeverk);
    }

    @Cacheable(value = CACHE_KODEVERK_2, unless = "#result.koder?.size() == 0")
    @GetMapping("/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public Mono<KodeverkAdjusted> getKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {

        return kodeverkService.getKodeverkByName(kodeverkNavn);
    }
}