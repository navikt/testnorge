package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingSimple;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.service.MalBestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING_MAL;
import static no.nav.dolly.config.CachingConfig.CACHE_LEGACY_BESTILLING_MAL;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequestMapping(value = "/api/v1/malbestilling")
@RestController
@RequiredArgsConstructor
public class MalBestillingController {

    private final MalBestillingService malBestillingService;

    @CacheEvict(value = {CACHE_BESTILLING_MAL, CACHE_LEGACY_BESTILLING_MAL}, allEntries = true)
    @PostMapping(value = "/ident/{ident}")
    @Operation(description = "Opprett ny mal-bestilling fra ident")
    public Mono<BestillingMal> createTemplateFromIdent(@PathVariable String ident,
                                                                 @RequestParam String malNavn) {

        return malBestillingService.createFromIdent(ident, malNavn);
    }

    @Cacheable(value = CACHE_LEGACY_BESTILLING_MAL)
    @GetMapping
    @Transactional(readOnly = true)
    @Operation(description = "Hent mal-bestilling, kan filtreres på en bruker")
    public Mono<RsMalBestillingWrapper> getMalBestillinger(@RequestParam(required = false) String brukerId) {

        return isBlank(brukerId) ?
                malBestillingService.getMalBestillinger() : malBestillingService.getMalbestillingByUser(brukerId);
    }

    @Cacheable(value = CACHE_BESTILLING_MAL)
    @GetMapping("/brukerId/{brukerId}")
    @Transactional(readOnly = true)
    @Operation(description = "Hent mal-bestilling, for angitt brukerId, evt ALLE eller FELLES (for anonyme maler)")
    public Flux<RsMalBestilling> getMalBestillingerBrukerId(@PathVariable("brukerId") String brukerId) {

        return malBestillingService.getMalBestillingerBrukerId(brukerId);
    }

    @GetMapping("/oversikt")
    @Transactional(readOnly = true)
    @Operation(description = "Hent oversikt bestillinger")
    public Mono<RsMalBestillingSimple> getMalBestillinger() {

        return malBestillingService.getMalBestillingOversikt();
    }

    @CacheEvict(value = {CACHE_BESTILLING_MAL, CACHE_LEGACY_BESTILLING_MAL}, allEntries = true)
    @PostMapping
    @Operation(description = "Opprett ny mal-bestilling fra bestillingId")
    @Transactional
    public Mono<BestillingMal> opprettMalbestilling(@RequestParam Long bestillingId, @RequestParam String malNavn) {

        return malBestillingService.saveBestillingMalFromBestillingId(bestillingId, malNavn);
    }

    @CacheEvict(value = {CACHE_BESTILLING_MAL, CACHE_LEGACY_BESTILLING_MAL}, allEntries = true)
    @DeleteMapping("/id/{id}")
    @Operation(description = "Slett mal-bestilling")
    @Transactional
    public Mono<Void> deleteMalBestilling(@PathVariable Long id) {

        return malBestillingService.deleteMalBestillingByID(id);
    }

    @CacheEvict(value = {CACHE_BESTILLING_MAL, CACHE_LEGACY_BESTILLING_MAL}, allEntries = true)
    @PutMapping("/id/{id}")
    @Operation(description = "Rediger mal-bestilling")
    @Transactional
    public Mono<BestillingMal> redigerMalBestilling(@PathVariable Long id, @RequestParam String malNavn) {

        return malBestillingService.updateMalNavnById(id, malNavn);
    }
}
