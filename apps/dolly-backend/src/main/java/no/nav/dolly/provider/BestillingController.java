package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.GjenopprettBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.projection.RsBestillingFragment;
import no.nav.dolly.domain.resultset.entity.bestilling.BestillingStatusEvent;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.service.BestillingEventPublisher;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.NavigasjonService;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bestilling", produces = MediaType.APPLICATION_JSON_VALUE)
public class BestillingController {

    private final BestillingService bestillingService;
    private final BestillingEventPublisher bestillingEventPublisher;
    private final GjenopprettBestillingService gjenopprettBestillingService;
    private final MapperFacade mapperFacade;
    private final NavigasjonService navigasjonService;
    private final OrganisasjonBestillingService organisasjonBestillingService;

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/{bestillingId}")
    @Operation(description = "Hent Bestilling med bestillingsId")
    public Mono<RsBestillingStatus> getBestillingById(@PathVariable("bestillingId") Long bestillingId) {

        return bestillingService.fetchBestillingById(bestillingId)
                .map(bestilling -> mapperFacade.map(bestilling, RsBestillingStatus.class));
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @DeleteMapping("/{bestillingId}")
    @Operation(description = "Slett Bestilling med bestillingsId")
    public Mono<Void> deleteBestillingById(@PathVariable("bestillingId") Long bestillingId) {

        return bestillingService.slettBestillingByBestillingId(bestillingId);
    }

    @GetMapping("/soekBestilling")
    @Operation(description = "Hent Bestillinger basert på fragment")
    public Flux<RsBestillingFragment> getBestillingerByFragment(@RequestParam(value = "fragment") String fragment) {

        return bestillingService.fetchBestillingByFragment(fragment);
    }

    @Operation(description = "Naviger til ønsket bestilling")
    @Transactional
    @GetMapping("/naviger/{bestillingId}")
    public Mono<RsWhereAmI> navigerTilBestilling(@PathVariable Long bestillingId,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        return navigasjonService.navigerTilBestilling(bestillingId, pageSize);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/gruppe/{gruppeId}")
    @Operation(description = "Hent bestillinger tilhørende en gruppe med gruppeId")
    public Flux<RsBestillingStatus> getBestillinger(@PathVariable("gruppeId") Long gruppeId,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(value = "pageSize", required = false, defaultValue = "2000") Integer pageSize) {

        return bestillingService.getBestillingerFromGruppeIdPaginert(gruppeId, page, pageSize)
                .map(bestilling -> mapperFacade.map(bestilling, RsBestillingStatus.class));
    }

    @GetMapping("/gruppe/{gruppeId}/ikkeferdig")
    @Operation(description = "Hent bestillinger tilhørende en gruppe med gruppeId")
    public Flux<RsBestillingStatus> getIkkeFerdigBestillinger(@PathVariable("gruppeId") Long gruppeId) {

        return bestillingService.fetchBestillingerByGruppeIdOgIkkeFerdig(gruppeId)
                .map(bestilling -> mapperFacade.map(bestilling, RsBestillingStatus.class));
    }

    @GetMapping("/gruppe/{gruppeId}/miljoer")
    @Operation(description = "Hent alle bestilte miljøer for en gruppe med gruppeId")
    public Mono<Set<String>> getAlleBestilteMiljoer(@PathVariable("gruppeId") Long gruppeId) {

        return bestillingService.fetchBestilteMiljoerByGruppeId(gruppeId);
    }

    @GetMapping("/miljoer")
    @Operation(description = "Hent alle bestilte miljøer for en liste med bestillingsIder")
    public Mono<Set<String>> getBestilteMiljoerByIds(@RequestParam("bestillingIds") java.util.List<Long> bestillingIds) {

        return bestillingService.fetchBestilteMiljoerByIds(bestillingIds);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @DeleteMapping("/stop/{bestillingId}")
    @Operation(description = "Stopp en Bestilling med bestillingsId")
    @Transactional
    public Mono<RsBestillingStatus> stopBestillingProgress(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "organisasjonBestilling", required = false) Boolean organisasjonBestilling) {

        return (isTrue(organisasjonBestilling)
                ? organisasjonBestillingService.cancelBestilling(bestillingId)
                : bestillingService.cancelBestilling(bestillingId))
                .map(bestilling -> mapperFacade.map(bestilling, RsBestillingStatus.class));
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @PostMapping("/gjenopprett/{bestillingId}")
    @Operation(description = "Gjenopprett en bestilling med bestillingsId, for en liste med miljoer")
    @Transactional
    public Mono<RsBestillingStatus> gjenopprettBestilling(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "miljoer", required = false) String miljoer) {

        return bestillingService.createBestillingForGjenopprettFraBestilling(bestillingId, miljoer)
                .map(bestilling -> {
                    gjenopprettBestillingService.executeAsync(bestilling);
                    return mapperFacade.map(bestilling, RsBestillingStatus.class);
                });
    }

    @GetMapping(value = "/{bestillingId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "Strøm sanntidsstatus for en bestilling via Server-Sent Events")
    public Flux<ServerSentEvent<BestillingStatusEvent>> streamBestillingStatus(
            @PathVariable("bestillingId") Long bestillingId) {

        return bestillingService.fetchBestillingStatusEvent(bestillingId)
                .flatMapMany(initial -> {
                    var initialSse = toSse(initial);

                    if (initial.ferdig()) {
                        return Flux.just(initialSse);
                    }

                    var updates = bestillingEventPublisher.subscribe(bestillingId)
                            .sample(Duration.ofSeconds(1))
                            .concatMap(id -> bestillingService.fetchBestillingStatusEvent(bestillingId)
                                    .onErrorResume(e -> {
                                        log.warn("Feil ved henting av bestilling-status for {}: {}", bestillingId, e.getMessage());
                                        return Mono.empty();
                                    }))
                            .map(this::toSse);

                    var heartbeat = Flux.interval(Duration.ofSeconds(15))
                            .map(tick -> ServerSentEvent.<BestillingStatusEvent>builder()
                                    .comment("heartbeat")
                                    .build());

                    return Flux.merge(Flux.concat(Flux.just(initialSse), updates), heartbeat)
                            .takeUntil(sse -> "completed".equals(sse.event()));
                });
    }

    @GetMapping(value = "/gruppe/{gruppeId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "Strøm oppdateringer for aktive bestillinger i en gruppe via Server-Sent Events")
    public Flux<ServerSentEvent<BestillingStatusEvent>> streamGruppeBestillinger(
            @PathVariable("gruppeId") Long gruppeId) {

        return bestillingService.fetchBestillingerByGruppeIdOgIkkeFerdig(gruppeId)
                .map(Bestilling::getId)
                .collect(Collectors.toSet())
                .flatMapMany(activeIds -> {
                    if (activeIds.isEmpty()) {
                        return Flux.empty();
                    }

                    var initial = Flux.fromIterable(activeIds)
                            .concatMap(bestillingService::fetchBestillingStatusEvent)
                            .map(this::toSse);

                    var updates = bestillingEventPublisher.subscribeAny(activeIds)
                            .sample(Duration.ofSeconds(2))
                            .concatMap(id -> bestillingService.fetchBestillingStatusEvent(id)
                                    .onErrorResume(e -> {
                                        log.warn("Feil ved henting av bestilling-status for {} i gruppe {}: {}", id, gruppeId, e.getMessage());
                                        return Mono.empty();
                                    }))
                            .map(this::toSse);

                    var heartbeat = Flux.interval(Duration.ofSeconds(15))
                            .map(tick -> ServerSentEvent.<BestillingStatusEvent>builder()
                                    .comment("heartbeat")
                                    .build());

                    var completedIds = new java.util.concurrent.ConcurrentHashMap<Long, Boolean>();
                    activeIds.forEach(id -> completedIds.put(id, false));

                    return Flux.merge(Flux.concat(initial, updates), heartbeat)
                            .doOnNext(sse -> {
                                if ("completed".equals(sse.event()) && nonNull(sse.data())) {
                                    completedIds.put(sse.data().id(), true);
                                }
                            })
                            .takeUntil(sse -> completedIds.values().stream().allMatch(Boolean::booleanValue));
                });
    }

    private ServerSentEvent<BestillingStatusEvent> toSse(BestillingStatusEvent event) {
        return ServerSentEvent.<BestillingStatusEvent>builder()
                .event(event.ferdig() ? "completed" : "progress")
                .data(event)
                .build();
    }
}