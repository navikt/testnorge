package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.strategy.JsonBestillingMapper;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class OrganisasjonBestillingService {

    private final OrganisasjonBestillingRepository bestillingRepository;
    private final OrganisasjonProgressService progressService;
    private final BrukerService brukerService;
    private final ObjectMapper objectMapper;
    private final JsonBestillingMapper jsonBestillingMapper;

    public RsOrganisasjonBestillingStatus fetchBestillingStatusById(Long bestillingId) {
        OrganisasjonBestilling bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(format("Fant ikke bestilling på bestillingId %d", bestillingId)));

        List<OrganisasjonBestillingProgress> bestillingProgress =
                progressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId);

        return RsOrganisasjonBestillingStatus.builder()
                .status(bestillingProgress)
                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                .sistOppdatert(bestilling.getSistOppdatert())
                .organisasjonNummer(bestillingProgress.get(0).getOrganisasjonsnummer())
                .id(bestillingId)
                .ferdig(bestilling.getFerdig())
                .feil(bestilling.getFeil())
                .environments(Arrays.asList(bestilling.getMiljoer().split(",")))
                .antallLevert(bestilling.getAntall())
                .build();
    }

    public List<RsOrganisasjonBestillingStatus> fetchBestillingStatusByBrukerId(String brukerId) {

        List<OrganisasjonBestillingProgress> bestillingProgress = progressService.fetchOrganisasjonBestillingProgressByBrukerId(brukerId);

        List<RsOrganisasjonBestillingStatus> statusListe = new ArrayList<>();
        bestillingProgress.forEach(bestillingStatus -> {

                    OrganisasjonBestilling orgBestilling = bestillingRepository.findById(bestillingStatus.getBestillingId()).orElseThrow(() ->
                            new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                                    "Fant ikke noen bestillinger med bestillingId: " + bestillingStatus.getBestillingId())
                    );
                    statusListe.add(RsOrganisasjonBestillingStatus.builder()
                            .status(singletonList(bestillingStatus))
                            .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(orgBestilling.getBestKriterier()))
                            .sistOppdatert(orgBestilling.getSistOppdatert())
                            .organisasjonNummer(bestillingStatus.getOrganisasjonsnummer())
                            .id(bestillingStatus.getBestillingId())
                            .ferdig(orgBestilling.getFerdig())
                            .feil(orgBestilling.getFeil())
                            .environments(Arrays.asList(orgBestilling.getMiljoer().split(",")))
                            .antallLevert(orgBestilling.getAntall())
                            .build());

                }
        );
        return statusListe;
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
                        .bruker(brukerService.fetchOrCreateBruker(getUserId()))
                        .build());
    }

    @Transactional
    public void setBestillingFeil(Long bestillingId, String feil) {
        Optional<OrganisasjonBestilling> byId = bestillingRepository.findById(bestillingId);

        byId.ifPresent(bestilling -> {
            bestilling.setFeil(feil);
            bestillingRepository.save(bestilling);
        });
    }

    @Transactional
    public void setBestillingFerdig(Long bestillingId) {
        Optional<OrganisasjonBestilling> byId = bestillingRepository.findById(bestillingId);

        byId.ifPresent(bestilling -> {
            bestilling.setFerdig(Boolean.TRUE);
            bestillingRepository.save(bestilling);
        });
    }

    @Transactional
    public void slettBestillingByBestillingId(Long bestillingId) {

        progressService.deleteByBestillingId(bestillingId);
        bestillingRepository.deleteBestillingWithNoChildren(bestillingId);
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