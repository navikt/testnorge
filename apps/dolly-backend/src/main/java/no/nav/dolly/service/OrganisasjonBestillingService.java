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
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingOrganisasjonStatusMapper;
import no.nav.dolly.mapper.strategy.JsonBestillingMapper;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class OrganisasjonBestillingService {

    private static final int BLOCK_SIZE = 50;

    private static final List<Status> DEPLOY_ENDED_STATUS_LIST = List.of(COMPLETED, ERROR, FAILED);

    private final BrukerRepository brukerRepository;
    private final OrganisasjonBestillingRepository bestillingRepository;
    private final OrganisasjonProgressService progressService;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final BrukerService brukerService;
    private final ObjectMapper objectMapper;
    private final JsonBestillingMapper jsonBestillingMapper;
    private final GetUserInfo getUserInfo;

    @Transactional
    public RsOrganisasjonBestillingStatus fetchBestillingStatusById(Long bestillingId) {

        OrganisasjonBestilling bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException("Fant ikke bestilling med id " + bestillingId));

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
                .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(bestillingProgress, nonNull(orgStatusList) ? orgStatusList : emptyList()))
                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                .sistOppdatert(bestilling.getSistOppdatert())
                .organisasjonNummer(bestillingProgress.getOrganisasjonsnummer())
                .id(bestillingId)
                .ferdig(isTrue(bestilling.getFerdig()))
                .feil(bestilling.getFeil())
                .environments(Arrays.asList(bestilling.getMiljoer().split(",")))
                .antallLevert(isTrue(bestilling.getFerdig()) && isBlank(bestilling.getFeil()) ? 1 : 0)
                .malBestillingNavn(bestilling.getMalBestillingNavn())
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
                        .environments(Arrays.asList(progress.getBestilling().getMiljoer().split(",")))
                        .antallLevert(isTrue(progress.getBestilling().getFerdig()) && isBlank(progress.getBestilling().getFeil()) ? 1 : 0)
                        .malBestillingNavn(progress.getBestilling().getMalBestillingNavn())
                        .build())
                .sorted((a, b) -> a.getSistOppdatert().isAfter(b.getSistOppdatert()) ? -1 : 1)
                .toList();
    }

    public List<OrganisasjonBestilling> fetchMalBestillinger() {
        return bestillingRepository.findMalBestilling();
    }

    public List<OrganisasjonBestilling> fetchMalbestillingByNavnAndUser(String brukerId, String malNavn) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        return nonNull(malNavn)
                ? bestillingRepository.findMalBestillingByMalnavnAndUser(bruker, malNavn)
                : bestillingRepository.findMalBestillingByUser(bruker);
    }

    @Transactional
    public OrganisasjonBestilling cancelBestilling(Long bestillingId) {

        Optional<OrganisasjonBestilling> bestillingById = bestillingRepository.findById(bestillingId);
        OrganisasjonBestilling organisasjonBestilling = bestillingById.orElseThrow(() -> new NotFoundException(format("Fant ikke organisasjon bestillingId %d", bestillingId)));

        organisasjonBestilling.setFeil("Bestilling stoppet");
        organisasjonBestilling.setFerdig(true);
        organisasjonBestilling.setSistOppdatert(now());
        saveBestillingToDB(organisasjonBestilling);
        return organisasjonBestilling;
    }

    @Transactional
    public OrganisasjonBestilling saveBestillingToDB(OrganisasjonBestilling bestilling) {

        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    @Transactional
    public OrganisasjonBestilling saveBestilling(RsOrganisasjonBestilling request) {

        return saveBestillingToDB(
                OrganisasjonBestilling.builder()
                        .antall(1)
                        .ferdig(false)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .bestKriterier(toJson(request.getOrganisasjon()))
                        .bruker(brukerService.fetchOrCreateBruker(getUserId(getUserInfo)))
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .build());
    }

    @Transactional
    public OrganisasjonBestilling saveBestilling(RsOrganisasjonBestillingStatus status) {

        return saveBestillingToDB(
                OrganisasjonBestilling.builder()
                        .antall(1)
                        .sistOppdatert(now())
                        .ferdig(isTrue(status.getFerdig()))
                        .miljoer(join(",", status.getEnvironments()))
                        .bestKriterier(toJson(status.getBestilling()))
                        .bruker(brukerService.fetchOrCreateBruker(getUserId(getUserInfo)))
                        .malBestillingNavn(status.getMalBestillingNavn())
                        .build());
    }

    @Transactional
    public void setBestillingFeil(Long bestillingId, String feil) {

        Optional<OrganisasjonBestilling> byId = bestillingRepository.findById(bestillingId);

        byId.ifPresent(bestilling -> {
            bestilling.setFeil(feil);
            bestilling.setFerdig(Boolean.TRUE);
            bestilling.setSistOppdatert(now());
            bestillingRepository.save(bestilling);
        });
    }

    @Transactional
    public void slettBestillingByOrgnummer(String orgnummer) {

        var progresser = progressService.findByOrganisasjonnummer(orgnummer);

        var bestillinger = progresser.stream()
                .map(OrganisasjonBestillingProgress::getBestilling)
                .collect(Collectors.toSet());

        progressService.deleteByOrgnummer(orgnummer);

        bestillinger.forEach(bestillingRepository::deleteBestillingWithNoChildren);
    }

    public List<OrganisasjonBestilling> fetchOrganisasjonBestillingByBrukerId(String brukerId) {

        var bruker = brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet med id " + brukerId));

        return bestillingRepository.findByBruker(bruker);
    }
    
    @Transactional
    public void redigerMalBestillingNavn(Long id, String malbestillingNavn) {

        Optional<OrganisasjonBestilling> token = bestillingRepository.findById(id);
        OrganisasjonBestilling bestilling = token.orElseThrow(() -> new NotFoundException(format("Id {%d} ikke funnet ", id)));
        bestilling.setMalBestillingNavn(malbestillingNavn);
    }

    public List<OrganisasjonDetaljer> getOrganisasjoner(String brukerId) {

        var orgnumre = fetchOrganisasjonBestillingByBrukerId(brukerId).stream()
                .sorted(Comparator.comparing(OrganisasjonBestilling::getSistOppdatert).reversed())
                .map(OrganisasjonBestilling::getProgresser)
                .flatMap(Collection::stream)
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .filter(orgnummer -> !"NA".equals(orgnummer))
                .distinct()
                .toList();

        return Flux.range(0, orgnumre.size() / BLOCK_SIZE + 1)
                .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                        orgnumre.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, orgnumre.size()))))
                .collectList()
                .block();
    }

    private void updateBestilling(OrganisasjonBestilling bestilling, List<OrgStatus> orgStatus) {

        var feil = orgStatus.stream()
                .filter(o -> FAILED.equals(o.getStatus()))
                .map(o -> o.getEnvironment() + ":" + o.getDetails())
                .collect(Collectors.joining(","));

        bestilling.setFeil(feil);

        var ferdig = orgStatus.stream()
                .anyMatch(o -> DEPLOY_ENDED_STATUS_LIST.stream().anyMatch(status -> status.equals(o.getStatus())));

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

        var orgStatus = organisasjonDeployStatus.getOrgStatus()
                .getOrDefault(bestillingProgress.getOrganisasjonsnummer(), emptyList());

        updateBestilling(bestilling, orgStatus);

        var forvalterStatus = orgStatus.stream()
                .map(org -> org.getEnvironment() + ":" + forvalterStatusDetails(org))
                .collect(Collectors.joining(","));
        bestillingProgress.setOrganisasjonsforvalterStatus(forvalterStatus);

        return orgStatus;
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