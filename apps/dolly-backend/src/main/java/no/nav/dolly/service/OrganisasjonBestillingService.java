package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployStatus;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployStatus.OrgStatus;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonStatusDTO.Status;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.mapper.BestillingOrganisasjonStatusMapper;
import no.nav.dolly.mapper.strategy.JsonBestillingMapper;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
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

    private static final List<Status> DEPLOY_ENDED_STATUS_LIST = List.of(COMPLETED, ERROR, FAILED);

    private final OrganisasjonBestillingRepository bestillingRepository;
    private final OrganisasjonProgressService progressService;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrganisasjonNummerService organisasjonNummerService;
    private final BrukerService brukerService;
    private final ObjectMapper objectMapper;
    private final JsonBestillingMapper jsonBestillingMapper;
    private final GetUserInfo getUserInfo;

    @Transactional(readOnly = true)
    public RsOrganisasjonBestillingStatus fetchBestillingStatusById(Long bestillingId) {

        OrganisasjonBestilling bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Fant ikke bestilling på bestillingId %d", bestillingId)));

        OrganisasjonBestillingProgress bestillingProgress;
        OrgStatus orgStatus = null;

        try {
            List<OrganisasjonBestillingProgress> bestillingProgressList = progressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId);

            if (bestillingProgressList.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }
            bestillingProgress = bestillingProgressList.get(0);

            if (isNotTrue(bestilling.getFerdig())) {
                orgStatus = getOrgforvalterStatus(bestilling, bestillingProgress);
            }

        } catch (WebClientResponseException e) {
            log.info("Status ikke opprettet for bestilling enda");
            return RsOrganisasjonBestillingStatus.builder().build();
        }

        return RsOrganisasjonBestillingStatus.builder()
                .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(bestillingProgress, nonNull(orgStatus) ? orgStatus.getDetails() : null))
                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                .sistOppdatert(bestilling.getSistOppdatert())
                .organisasjonNummer(bestillingProgress.getOrganisasjonsnummer())
                .id(bestillingId)
                .ferdig(isTrue(bestilling.getFerdig()))
                .feil(bestilling.getFeil())
                .environments(Arrays.asList(bestilling.getMiljoer().split(",")))
                .antallLevert(isTrue(bestilling.getFerdig()) && isBlank(bestilling.getFeil()) ? 1 : 0)
                .build();
    }


    public List<RsOrganisasjonBestillingStatus> fetchBestillingStatusByBrukerId(String brukerId) {

        List<OrganisasjonBestillingProgress> bestillingProgress;

        try {
            bestillingProgress = progressService.fetchOrganisasjonBestillingProgressByBrukerId(brukerId);
        } catch (WebClientResponseException e) {
            if (404 == e.getRawStatusCode()) {
                log.info("Brukeren har ingen bestilte organisasjoner");
            } else {
                log.info("Klarte ikke å hente organisasjon bestillinger på brukeren");
            }
            return emptyList();
        }

        List<RsOrganisasjonBestillingStatus> statusListe = new ArrayList<>();
        bestillingProgress.forEach(bestillingStatus -> {

                    OrganisasjonBestilling orgBestilling = bestillingRepository.findById(bestillingStatus.getBestillingId()).orElseThrow(() ->
                            new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                                    "Fant ikke noen bestillinger med bestillingId: " + bestillingStatus.getBestillingId())
                    );
                    statusListe.add(RsOrganisasjonBestillingStatus.builder()
                            .status(BestillingOrganisasjonStatusMapper.buildOrganisasjonStatusMap(bestillingStatus, null))
                            .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(orgBestilling.getBestKriterier()))
                            .sistOppdatert(orgBestilling.getSistOppdatert())
                            .organisasjonNummer(bestillingStatus.getOrganisasjonsnummer())
                            .id(bestillingStatus.getBestillingId())
                            .ferdig(isTrue(orgBestilling.getFerdig()))
                            .feil(orgBestilling.getFeil())
                            .environments(Arrays.asList(orgBestilling.getMiljoer().split(",")))
                            .antallLevert(isTrue(orgBestilling.getFerdig()) && isBlank(orgBestilling.getFeil()) ? 1 : 0)
                            .build());

                }
        );
        return statusListe.stream()
                .sorted(Comparator.comparingLong(RsOrganisasjonBestillingStatus::getId))
                .collect(Collectors.toList());
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
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .bestKriterier(toJson(request.getOrganisasjon()))
                        .bruker(brukerService.fetchOrCreateBruker(getUserId(getUserInfo)))
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
    public void setBestillingFerdig(Long bestillingId) {

        Optional<OrganisasjonBestilling> byId = bestillingRepository.findById(bestillingId);

        byId.ifPresent(bestilling -> {
            bestilling.setFerdig(Boolean.TRUE);
            bestilling.setSistOppdatert(now());
            bestillingRepository.save(bestilling);
        });
    }

    @Transactional
    public void slettBestillingByOrgnummer(String orgnummer) {

        List<Long> bestillinger = organisasjonNummerService.fetchBestillingsIdFromOrganisasjonNummer(orgnummer).stream()
                .map(OrganisasjonNummer::getBestillingId)
                .collect(Collectors.toList());

        organisasjonNummerService.deleteByOrgnummer(orgnummer);
        progressService.deleteByOrgnummer(orgnummer);

        bestillinger.forEach(bestillingRepository::deleteBestillingWithNoChildren);
    }

    private OrgStatus getOrgforvalterStatus(OrganisasjonBestilling bestilling, OrganisasjonBestillingProgress bestillingProgress) {
        OrgStatus orgStatus;
        OrganisasjonDeployStatus organisasjonDeployStatus = organisasjonConsumer.hentOrganisasjonStatus(Collections.singletonList(bestillingProgress.getOrganisasjonsnummer()));

        List<OrgStatus> organisasjonStatusList = organisasjonDeployStatus.getOrgStatus().values().stream().findFirst().orElse(emptyList());
        orgStatus = organisasjonStatusList.isEmpty() ? new OrgStatus() : organisasjonStatusList.get(0);
        OrgStatus finalOrgStatus = orgStatus;

        if (DEPLOY_ENDED_STATUS_LIST.stream().anyMatch(status -> status.equals(finalOrgStatus.getStatus()))) {
            if (ERROR.equals(orgStatus.getStatus()) || FAILED.equals(orgStatus.getStatus())) {
                log.error("Error i organisasjonForvalter: {}", orgStatus.getError());
                setBestillingFeil(bestilling.getId(), orgStatus.getError());
            }
            setBestillingFerdig(bestilling.getId());
        }
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
