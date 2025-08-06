package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployStatus.OrgStatus;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonStatusDTO.Status;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingOrganisasjonStatusMapper;
import no.nav.dolly.mapper.strategy.JsonBestillingMapper;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonStatusDTO.Status.COMPLETED;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonStatusDTO.Status.ERROR;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonStatusDTO.Status.FAILED;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class OrganisasjonBestillingService {

    private static final String FINNES_IKKE = "Fant ikke bestilling med id %d";
    private static final int BLOCK_SIZE = 50;

    private static final List<Status> DEPLOY_ENDED_STATUS_LIST = List.of(COMPLETED, ERROR, FAILED);

    private final BrukerRepository brukerRepository;
    private final BrukerService brukerService;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final JsonBestillingMapper jsonBestillingMapper;
    private final ObjectMapper objectMapper;
    private final OrganisasjonBestillingMalService organisasjonBestillingMalService;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrganisasjonProgressService organisasjonProgressService;

    @Transactional
    public Mono<RsOrganisasjonBestillingStatus> fetchBestillingStatusById(Long bestillingId) {

        return organisasjonBestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(bestillingId))))
                .flatMap(bestilling -> organisasjonProgressRepository.findByBestillingId(bestillingId)
                        .next()
                        .switchIfEmpty(Mono.just(OrganisasjonBestillingProgress.builder().build()))
                        .flatMap(bestillingProgress -> {
                            if (isNotTrue(bestilling.getFerdig())) {
                                return getOrgforvalterStatus(bestilling, bestillingProgress)
                                        .zipWith(Mono.just(bestillingProgress));
                            }
                            return Mono.just(List.of(OrgStatus.builder().build()))
                                    .zipWith(Mono.just(bestillingProgress));
                        })
                        .map(tuple -> RsOrganisasjonBestillingStatus.builder()
                                .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(tuple.getT2(), tuple.getT1()))
                                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                                .sistOppdatert(bestilling.getSistOppdatert())
                                .organisasjonNummer(tuple.getT2().getOrganisasjonsnummer())
                                .id(bestillingId)
                                .ferdig(isTrue(bestilling.getFerdig()))
                                .feil(bestilling.getFeil())
                                .environments(Set.of(bestilling.getMiljoer().split(",")))
                                .antallLevert(isTrue(bestilling.getFerdig()) && isBlank(bestilling.getFeil()) ? 1 : 0)
                                .build()));
    }

    public Flux<RsOrganisasjonBestillingStatus> fetchBestillingStatusByBrukerId(String brukerId) {

        return fetchOrganisasjonBestillingByBrukerId(brukerId)
                .flatMap(bestilling -> organisasjonProgressRepository.findByBestillingId(bestilling.getId())
                        .map(progress -> RsOrganisasjonBestillingStatus.builder()
                                .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(progress, emptyList()))
                                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                                .sistOppdatert(bestilling.getSistOppdatert())
                                .organisasjonNummer(progress.getOrganisasjonsnummer())
                                .id(bestilling.getId())
                                .ferdig(isTrue(bestilling.getFerdig()))
                                .feil(bestilling.getFeil())
                                .environments(Set.of(bestilling.getMiljoer().split(",")))
                                .antallLevert(isTrue(bestilling.getFerdig()) && isBlank(bestilling.getFeil()) ? 1 : 0)
                                .build())
                        .sort((a, b) -> a.getSistOppdatert().isAfter(b.getSistOppdatert()) ? -1 : 0));
    }

    @Transactional
    public Mono<OrganisasjonBestilling> cancelBestilling(Long bestillingId) {

        return organisasjonBestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(bestillingId))))
                .map(orgBestilling -> {

                    orgBestilling.setFeil("Bestilling stoppet");
                    orgBestilling.setFerdig(true);
                    orgBestilling.setSistOppdatert(now());
                    return orgBestilling;
                })
                .flatMap(organisasjonBestillingRepository::save);
    }

    @Transactional
    public Mono<OrganisasjonBestilling> saveBestilling(RsOrganisasjonBestilling request) {

        return getAuthenticatedUserId.call()
                .flatMap(brukerRepository::findByBrukerId)
                .map(bruker -> OrganisasjonBestilling.builder()
                        .antall(1)
                        .ferdig(false)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .bestKriterier(toJson(request.getOrganisasjon()))
                        .bruker(bruker)
                        .brukerId(bruker.getId())
                        .build())
                .flatMap(organisasjonBestillingRepository::save)
                .flatMap(bestilling -> {
                    if (isNotBlank(request.getMalBestillingNavn())) {
                        return organisasjonBestillingMalService.saveOrganisasjonBestillingMal(bestilling, request.getMalBestillingNavn())
                                .thenReturn(bestilling);
                    }
                    return Mono.just(bestilling);
                })
                .flatMap(bestilling -> Mono.just(OrganisasjonBestillingProgress.builder()
                                .bestillingId(bestilling.getId())
                                .organisasjonsnummer("Ubestemt")
                                .organisasjonsforvalterStatus(request.getEnvironments().stream()
                                        .map(env -> env + ":Pågående")
                                        .collect(Collectors.joining(",")))
                                .build())
                        .flatMap(organisasjonProgressRepository::save)
                        .thenReturn(bestilling));
    }

    @Transactional
    public Mono<OrganisasjonBestilling> saveBestilling(RsOrganisasjonBestillingStatus status) {

        return getAuthenticatedUserId.call()
                .flatMap(brukerService::fetchBruker)
                .flatMap(bruker -> Mono.just(
                        OrganisasjonBestilling.builder()
                                .antall(1)
                                .sistOppdatert(now())
                                .ferdig(isTrue(status.getFerdig()))
                                .miljoer(join(",", status.getEnvironments()))
                                .bestKriterier(toJson(status.getBestilling()))
                                .bruker(bruker)
                                .build()))
                .flatMap(organisasjonBestillingRepository::save);
    }

    @Transactional
    public Mono<Void> setBestillingFeil(Long bestillingId, String feil) {

        return organisasjonBestillingRepository.findById(bestillingId)
                .map(bestilling -> {
                    bestilling.setFeil(feil);
                    bestilling.setFerdig(Boolean.TRUE);
                    bestilling.setSistOppdatert(now());
                    return bestilling;
                })
                .flatMap(organisasjonBestillingRepository::save)
                .then();
    }

    @Transactional
    public Mono<Void> slettBestillingByOrgnummer(String orgnummer) {

        return organisasjonProgressService.findByOrganisasjonnummer(orgnummer)
                .map(OrganisasjonBestillingProgress::getBestillingId)
                .collectList()
                .flatMap(bestillinger -> organisasjonProgressRepository.deleteByOrganisasjonsnummer(orgnummer)
                        .collectList()
                        .then(Flux.fromIterable(bestillinger)
                                .flatMap(organisasjonBestillingRepository::deleteBestillingWithNoChildren)
                                .collectList()))
                .then();
    }

    public Flux<OrganisasjonBestilling> fetchOrganisasjonBestillingByBrukerId(String brukerId) {

        return brukerService.fetchBruker(brukerId)
                .map(Bruker::getId)
                .flatMapMany(organisasjonBestillingRepository::findByBrukerId);
    }

    public Flux<OrganisasjonDetaljer> getOrganisasjoner(String brukerId) {

        return fetchOrganisasjonBestillingByBrukerId(brukerId)
                .flatMap(bestilling -> organisasjonProgressRepository.findByBestillingId(bestilling.getId()))
                .sort(Comparator.comparing(OrganisasjonBestillingProgress::getId).reversed())
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .filter(orgnummer -> !"NA".equals(orgnummer))
                .distinct()
                .buffer(BLOCK_SIZE)
                .flatMap(organisasjonConsumer::hentOrganisasjon)
                .sort(Comparator.comparing(OrganisasjonDetaljer::getId).reversed());
    }

    private Mono<OrganisasjonBestilling> updateBestilling(OrganisasjonBestilling bestilling, List<OrgStatus> orgStatus) {

        var feil = orgStatus.stream()
                .filter(o -> FAILED.equals(o.getStatus()))
                .map(o -> o.getEnvironment() + ":" + o.getDetails())
                .collect(Collectors.joining(","));

        bestilling.setFeil(feil);

        var ferdig = !orgStatus.isEmpty() && orgStatus.stream()
                .allMatch(o -> DEPLOY_ENDED_STATUS_LIST.stream()
                        .anyMatch(status -> status.equals(o.getStatus()))) &&
                Arrays.stream(bestilling.getMiljoer().split(","))
                        .allMatch(miljoe -> orgStatus.stream()
                                .anyMatch(o -> o.getEnvironment().equals(miljoe)));

        bestilling.setFerdig(ferdig);
        bestilling.setSistOppdatert(now());

        return organisasjonBestillingRepository.save(bestilling);
    }

    private String forvalterStatusDetails(OrgStatus orgStatus) {
        if (isNull(orgStatus) || isNull(orgStatus.getStatus())) {
            return "OK";
        }
        return switch (orgStatus.getStatus()) {
            case COMPLETED -> "OK";
            case ERROR, FAILED -> "Feil-" + orgStatus.getDetails();
            default -> orgStatus.getStatus().name();
        };
    }

    private Mono<List<OrgStatus>> getOrgforvalterStatus(OrganisasjonBestilling bestilling, OrganisasjonBestillingProgress bestillingProgress) {

        return organisasjonConsumer.hentOrganisasjonStatus(List.of(bestillingProgress.getOrganisasjonsnummer()))
                .doOnNext(status ->
                        log.info("Status for org deploy på org: {} - {}", bestillingProgress.getOrganisasjonsnummer(), status))
                .switchIfEmpty(Mono.empty())
                .flatMap(organisasjonDeployStatus -> Mono.just(organisasjonDeployStatus.getOrgStatus()
                        .getOrDefault(bestillingProgress.getOrganisasjonsnummer(), emptyList())))
                .flatMap(orgStatus -> updateBestilling(bestilling, orgStatus)
                        .thenReturn(orgStatus))
                .flatMap(organisasjonDeployStatus -> {
                    var forvalterStatus = organisasjonDeployStatus.stream()
                            .map(org -> org.getEnvironment() + ":" + forvalterStatusDetails(org))
                            .collect(Collectors.joining(","));
                    bestillingProgress.setOrganisasjonsforvalterStatus(forvalterStatus);
                    return organisasjonProgressRepository.save(bestillingProgress)
                            .thenReturn(organisasjonDeployStatus);
                });
    }

    public Mono<Void> slettBestillingById(Long bestillingId) {

        return organisasjonBestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(bestillingId))))
                .flatMap(ignore -> organisasjonProgressRepository.deleteByBestillingId(bestillingId))
                .then(organisasjonBestillingRepository.deleteBestillingWithNoChildren(bestillingId));
    }

    private String toJson(Object object) {

        try {
            if (nonNull(object)) {
                return objectMapper.writer().writeValueAsString(object);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            log.info("Konvertering til Json feilet", e);
        }
        return null;
    }
}