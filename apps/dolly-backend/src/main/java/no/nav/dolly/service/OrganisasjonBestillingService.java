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
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingOrganisasjonStatusMapper;
import no.nav.dolly.mapper.strategy.JsonBestillingMapper;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
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

    private static final int BLOCK_SIZE = 50;

    private static final List<Status> DEPLOY_ENDED_STATUS_LIST = List.of(COMPLETED, ERROR, FAILED);

    private final BrukerRepository brukerRepository;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonBestillingMalService organisasjonBestillingMalService;
    private final OrganisasjonProgressService progressService;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final BrukerService brukerService;
    private final ObjectMapper objectMapper;
    private final JsonBestillingMapper jsonBestillingMapper;

    @Transactional
    public Mono<RsOrganisasjonBestillingStatus> fetchBestillingStatusById(Long bestillingId) {

        var test = organisasjonBestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Fant ikke bestilling med id " + bestillingId)))

        OrganisasjonBestillingProgress bestillingProgress;
        List<OrgStatus> orgStatusList = null;

        try {
            bestillingProgress = progressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId)
                    .stream().findFirst().orElseThrow(() -> new NotFoundException("Status ikke funnet for bestillingId " + bestillingId));

            if (isNotTrue(bestilling.getFerdig())) {
                orgStatusList = getOrgforvalterStatus(bestilling, bestillingProgress);
            }

        } catch (WebClientResponseException e) {
            log.info("Status ennå ikke opprettet for bestilling");
            return RsOrganisasjonBestillingStatus.builder().build();
        }

        return RsOrganisasjonBestillingStatus.builder()
                .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(bestillingProgress, orgStatusList))
                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                .sistOppdatert(bestilling.getSistOppdatert())
                .organisasjonNummer(bestillingProgress.getOrganisasjonsnummer())
                .id(bestillingId)
                .ferdig(isTrue(bestilling.getFerdig()))
                .feil(bestilling.getFeil())
                .environments(Set.of(bestilling.getMiljoer().split(",")))
                .antallLevert(isTrue(bestilling.getFerdig()) && isBlank(bestilling.getFeil()) ? 1 : 0)
                .build();
    }

    public List<RsOrganisasjonBestillingStatus> fetchBestillingStatusByBrukerId(String brukerId) {

        var bestillinger = fetchOrganisasjonBestillingByBrukerId(brukerId);

        return bestillinger.stream()
                .map(OrganisasjonBestilling::getProgresser)
                .flatMap(Collection::stream)
                .map(progress -> RsOrganisasjonBestillingStatus.builder()
                        .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(progress, emptyList()))
                        .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(progress.getBestilling().getBestKriterier()))
                        .sistOppdatert(progress.getBestilling().getSistOppdatert())
                        .organisasjonNummer(progress.getOrganisasjonsnummer())
                        .id(progress.getBestilling().getId())
                        .ferdig(isTrue(progress.getBestilling().getFerdig()))
                        .feil(progress.getBestilling().getFeil())
                        .environments(Set.of(progress.getBestilling().getMiljoer().split(",")))
                        .antallLevert(isTrue(progress.getBestilling().getFerdig()) && isBlank(progress.getBestilling().getFeil()) ? 1 : 0)
                        .build())
                .sorted((a, b) -> a.getSistOppdatert().isAfter(b.getSistOppdatert()) ? -1 : 1)
                .toList();
    }

    @Transactional
    public Mono<OrganisasjonBestilling> cancelBestilling(Long bestillingId) {

        return organisasjonBestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error( new NotFoundException(format("Fant ikke bestilling med id %d", bestillingId))))
                        .map(orgBestilling -> {

                                    orgBestilling.setFeil("Bestilling stoppet");
                                    orgBestilling.setFerdig(true);
                                    orgBestilling.setSistOppdatert(now());
                                    return orgBestilling;
                                })
                                .flatMap(organisasjonBestillingRepository::save);
    }

    @Transactional
    public OrganisasjonBestilling saveBestillingToDB(OrganisasjonBestilling bestilling) {

        try {
            return organisasjonBestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    @Transactional
    public OrganisasjonBestilling saveBestilling(RsOrganisasjonBestilling request) {

        Bruker bruker = brukerService.fetchOrCreateBruker();
        OrganisasjonBestilling bestilling = OrganisasjonBestilling.builder()
                .antall(1)
                .ferdig(false)
                .sistOppdatert(now())
                .miljoer(join(",", request.getEnvironments()))
                .bestKriterier(toJson(request.getOrganisasjon()))
                .bruker(bruker)
                .build();

        if (isNotBlank(request.getMalBestillingNavn())) {
            organisasjonBestillingMalService.saveOrganisasjonBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }

        return saveBestillingToDB(bestilling);
    }

    @Transactional
    public OrganisasjonBestilling saveBestilling(RsOrganisasjonBestillingStatus status) {

//        Bruker bruker = brukerService.fetchOrCreateBruker();
        OrganisasjonBestilling bestilling = OrganisasjonBestilling.builder()
                .antall(1)
                .sistOppdatert(now())
                .ferdig(isTrue(status.getFerdig()))
                .miljoer(join(",", status.getEnvironments()))
                .bestKriterier(toJson(status.getBestilling()))
//                .bruker(bruker)
                .build();

        return saveBestillingToDB(bestilling);
    }

    @Transactional
    public void setBestillingFeil(Long bestillingId, String feil) {

        Optional<OrganisasjonBestilling> byId = organisasjonBestillingRepository.findById(bestillingId);

        byId.ifPresent(bestilling -> {
            bestilling.setFeil(feil);
            bestilling.setFerdig(Boolean.TRUE);
            bestilling.setSistOppdatert(now());
            organisasjonBestillingRepository.save(bestilling);
        });
    }

    @Transactional
    public void slettBestillingByOrgnummer(String orgnummer) {

        var progresser = progressService.findByOrganisasjonnummer(orgnummer);

        var bestillinger = progresser.stream()
                .map(OrganisasjonBestillingProgress::getBestilling)
                .collect(Collectors.toSet());

        progressService.deleteByOrgnummer(orgnummer);

        bestillinger.forEach(organisasjonBestillingRepository::deleteBestillingWithNoChildren);
    }

    public List<OrganisasjonBestilling> fetchOrganisasjonBestillingByBrukerId(String brukerId) {

        var bruker = isNull(brukerId) ? brukerService.fetchOrCreateBruker() :
                brukerRepository.findByBrukerId(brukerId)
                        .orElseThrow(() -> new NotFoundException("Bruker ikke funnet med id " + brukerId));

//        return organisasjonBestillingRepository.findByBruker(bruker);
        return emptyList(); // TBD
    }

    public List<OrganisasjonDetaljer> getOrganisasjoner(String brukerId) {

        var orgnumre = fetchOrganisasjonBestillingByBrukerId(brukerId).stream()
                .map(OrganisasjonBestilling::getProgresser)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(OrganisasjonBestillingProgress::getId).reversed())
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .filter(orgnummer -> !"NA".equals(orgnummer))
                .distinct()
                .toList();

        return Flux.range(0, orgnumre.size() / BLOCK_SIZE + 1)
                .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                        orgnumre.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, orgnumre.size()))))
                .sort(Comparator.comparing(OrganisasjonDetaljer::getId).reversed())
                .collectList()
                .block();
    }

    private void updateBestilling(OrganisasjonBestilling bestilling, List<OrgStatus> orgStatus) {

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

    private List<OrgStatus> getOrgforvalterStatus(OrganisasjonBestilling bestilling, OrganisasjonBestillingProgress bestillingProgress) {

        var organisasjonDeployStatus = organisasjonConsumer.hentOrganisasjonStatus(List.of(bestillingProgress.getOrganisasjonsnummer()));

        log.info("Status for org deploy på org: {} - {}", bestillingProgress.getOrganisasjonsnummer(), organisasjonDeployStatus);

        if (nonNull(organisasjonDeployStatus)) {
            var orgStatus = organisasjonDeployStatus.getOrgStatus()
                    .getOrDefault(bestillingProgress.getOrganisasjonsnummer(), emptyList());

            updateBestilling(bestilling, orgStatus);

            var forvalterStatus = orgStatus.stream()
                    .map(org -> org.getEnvironment() + ":" + forvalterStatusDetails(org))
                    .collect(Collectors.joining(","));
            bestillingProgress.setOrganisasjonsforvalterStatus(forvalterStatus);
            return orgStatus;

        } else {
            return emptyList();
        }
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