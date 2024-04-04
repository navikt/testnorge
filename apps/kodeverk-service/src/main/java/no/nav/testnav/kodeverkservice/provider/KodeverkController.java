package no.nav.testnav.kodeverkservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.kodeverkservice.service.KodeverkService;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static no.nav.testnav.kodeverkservice.config.CacheConfig.CACHE_KODEVERK;
import static no.nav.testnav.kodeverkservice.config.CacheConfig.CACHE_KODEVERK_2;

@RestController
@RequestMapping("/api/v1/kodeverk")
@Validated
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @Cacheable(value = CACHE_KODEVERK, unless = "#result.kodeverk?.size() == 0")
    @GetMapping
    @Operation(description = "Hent kodeverk, returnerer map")
    public Mono<KodeverkDTO> fetchKodeverk(@RequestParam String kodeverk) {

        return kodeverkService.getKodeverkMap(kodeverk);
    }

    @Cacheable(value = CACHE_KODEVERK_2, unless = "#result.koder?.size() == 0")
    @GetMapping("/{kodeverkNavn}")
    @Operation(description = "Hent kodeverk etter kodeverkNavn")
    public Mono<KodeverkAdjustedDTO> getKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {

        return kodeverkService.getKodeverkByName(kodeverkNavn);
    }
}