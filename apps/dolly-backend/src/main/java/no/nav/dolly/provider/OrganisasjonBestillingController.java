package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonClient;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper;
import no.nav.dolly.service.BestillingEventPublisher;
import no.nav.dolly.service.OrganisasjonBestillingMalService;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon/bestilling")
public class OrganisasjonBestillingController {

    private final OrganisasjonClient organisasjonClient;
    private final BestillingEventPublisher bestillingEventPublisher;
    private final OrganisasjonBestillingService bestillingService;
    private final OrganisasjonBestillingMalService organisasjonBestillingMalService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(description = "Opprett organisasjon")
    @Transactional
    public Mono<RsOrganisasjonBestillingStatus> opprettOrganisasjonBestilling(@RequestBody RsOrganisasjonBestilling request) {

        return bestillingService.saveBestilling(request)
                .flatMap(bestilling ->
                        organisasjonClient.opprett(request, bestilling)
                                .doOnSuccess(v -> bestillingService.monitorDeployCompletion(bestilling.getId()))
                                .then(getStatus(bestilling, "Ubestemt")));
    }

    @GetMapping
    @Operation(description = "Hent status på bestilling basert på bestillingId")
    public Mono<RsOrganisasjonBestillingStatus> hentBestilling(
            @Parameter(description = "ID på bestilling av organisasjon", example = "123") @RequestParam Long bestillingId) {

        return bestillingService.fetchBestillingStatusById(bestillingId);
    }

    @DeleteMapping("/{orgnummer}")
    @Operation(description = "Slett bestilling ved orgnummer")
    @Transactional
    public Mono<Void> slettBestillingByOrgnummer(@PathVariable("orgnummer") String orgnummer) {

        return bestillingService.slettBestillingByOrgnummer(orgnummer);
    }

    @DeleteMapping("/id/{id}")
    @Operation(description = "Slett bestilling ved bestillingId")
    @Transactional
    public Mono<Void> slettBestillingById(@PathVariable("id") Long id) {

        return bestillingService.slettBestillingById(id);
    }

    @GetMapping("/bestillingsstatus")
    @Operation(description = "Hent status på bestilling basert på brukerId")
    public Flux<RsOrganisasjonBestillingStatus> hentBestillingStatus(
            @Parameter(description = "BrukerID som er unik til en Azure bruker",
                    example = "1k9242uc-638g-1234-5678-7894k0j7lu6n") @RequestParam String brukerId) {

        return bestillingService.fetchBestillingStatusByBrukerId(brukerId);
    }

    @GetMapping("/malbestilling")
    @Operation(description = "Hent mal-bestilling")
    public Mono<RsOrganisasjonMalBestillingWrapper> getMalBestillinger(@RequestParam(required = false, value = "brukerId") String brukerId) {

        return isBlank(brukerId) ?
                organisasjonBestillingMalService.getOrganisasjonMalBestillinger() :
                organisasjonBestillingMalService.getMalbestillingerByUser(brukerId);
    }

    @CacheEvict(value = {CACHE_BESTILLING}, allEntries = true)
    @PostMapping("/malbestilling")
    @Operation(description = "Opprett ny mal-bestilling fra bestillingId")
    @Transactional
    public Mono<OrganisasjonBestillingMal> opprettMalbestilling(Long bestillingId, String malNavn) {

        return organisasjonBestillingMalService.saveOrganisasjonBestillingMalFromBestillingId(bestillingId, malNavn);
    }

    @DeleteMapping("/malbestilling/{id}")
    @Operation(description = "Slett mal-bestilling")
    @Transactional
    public Mono<Void> deleteMalBestilling(@PathVariable Long id) {

        return organisasjonBestillingMalService.deleteOrganisasjonMalbestillingById(id);
    }

    @PutMapping("/malbestilling/{id}")
    @Operation(description = "Rediger mal-bestilling")
    @Transactional
    public Mono<OrganisasjonBestillingMal> redigerMalBestilling(@PathVariable Long id, @RequestParam(value = "malNavn") String malNavn) {

        return organisasjonBestillingMalService.updateOrganisasjonMalNavnById(id, malNavn);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "Strøm sanntidsstatus for en organisasjon-bestilling via Server-Sent Events")
    public Flux<ServerSentEvent<RsOrganisasjonBestillingStatus>> streamBestillingStatus(
            @RequestParam Long bestillingId) {

        return bestillingService.fetchBestillingSnapshot(bestillingId)
                .flatMapMany(initial -> {
                    if (Boolean.TRUE.equals(initial.getFerdig())) {
                        return Flux.just(toOrgSse(initial));
                    }

                    var updates = bestillingEventPublisher.subscribeOrg(bestillingId)
                            .concatMap(id -> bestillingService.fetchBestillingSnapshot(bestillingId)
                                    .onErrorResume(e -> {
                                        log.warn("Feil ved henting av org-bestilling-status for {}: {}", bestillingId, e.getMessage());
                                        return Mono.empty();
                                    }))
                            .map(this::toOrgSse);

                    var fallbackCheck = Flux.interval(Duration.ofSeconds(15))
                            .concatMap(tick -> bestillingService.fetchBestillingSnapshot(bestillingId)
                                    .onErrorResume(e -> Mono.empty()))
                            .map(this::toOrgSse);

                    return Flux.merge(Flux.concat(Flux.just(toOrgSse(initial)), updates), fallbackCheck)
                            .takeUntil(sse -> "completed".equals(sse.event()))
                            .take(Duration.ofMinutes(30));
                });
    }

    private ServerSentEvent<RsOrganisasjonBestillingStatus> toOrgSse(RsOrganisasjonBestillingStatus status) {
        return ServerSentEvent.<RsOrganisasjonBestillingStatus>builder()
                .event(Boolean.TRUE.equals(status.getFerdig()) ? "completed" : "progress")
                .data(status)
                .build();
    }

    static Mono<RsOrganisasjonBestillingStatus> getStatus(OrganisasjonBestilling bestilling, String orgnummer) {

        return Mono.just(RsOrganisasjonBestillingStatus.builder()
                .id(bestilling.getId())
                .sistOppdatert(bestilling.getSistOppdatert())
                .antallLevert(0)
                .ferdig(false)
                .organisasjonNummer(orgnummer)
                .status(List.of(RsOrganisasjonStatusRapport.builder()
                        .id(SystemTyper.ORGANISASJON_FORVALTER)
                        .navn(SystemTyper.ORGANISASJON_FORVALTER.getBeskrivelse())
                        .statuser(List.of(RsOrganisasjonStatusRapport.Status.builder()
                                .melding("Bestilling startet ...")
                                .detaljert(Arrays.stream(bestilling.getMiljoer().split(","))
                                        .map(miljoe -> RsOrganisasjonStatusRapport.Detaljert.builder()
                                                .orgnummer(orgnummer)
                                                .miljo(miljoe)
                                                .build())
                                        .toList())
                                .build()))
                        .build()))
                .build());
    }
}
