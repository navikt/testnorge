package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.mapper.strategy.JsonBestillingMapper;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_ORG_BESTILLING;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class OrganisasjonBestillingService {

    private final OrganisasjonBestillingRepository bestillingRepository;
    private final OrganisasjonProgressService progressService;
    private final OrganisasjonNummerService organisasjonNummerService;
    private final BrukerService brukerService;
    private final ObjectMapper objectMapper;
    private final JsonBestillingMapper jsonBestillingMapper;

    public RsOrganisasjonBestillingStatus fetchBestillingStatusById(Long bestillingId) {

        OrganisasjonBestilling bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Fant ikke bestilling på bestillingId %d", bestillingId)));

        List<OrganisasjonBestillingProgress> bestillingProgressList = new ArrayList<>();

        try {
            bestillingProgressList = progressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId);

        } catch (HttpClientErrorException e) {
            log.info("Status ikke opprettet for bestilling enda");
        }

        return RsOrganisasjonBestillingStatus.builder()
                .status(singletonList(RsOrganisasjonStatusRapport.builder()
                        .navn("Organisasjon Forvalter")
                        .id(SystemTyper.ORGANISASJON_FORVALTER)
                        .statuser(singletonList(RsOrganisasjonStatusRapport.Status.builder()
                                .orgnummer(!bestillingProgressList.isEmpty() ? bestillingProgressList.get(0).getOrganisasjonsnummer() : null)
                                .melding(!bestillingProgressList.isEmpty() ? bestillingProgressList.get(0).getOrganisasjonsforvalterStatus() : null)
                                .detaljert(singletonList(RsOrganisasjonStatusRapport.Detaljert.builder()
                                        .miljo(bestilling.getMiljoer())
                                        .orgnummer(!bestillingProgressList.isEmpty() ? bestillingProgressList.get(0).getOrganisasjonsnummer() : null)
                                        .build()))
                                .build()))
                        .build()))
                .bestilling(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()))
                .sistOppdatert(bestilling.getSistOppdatert())
                .organisasjonNummer(!bestillingProgressList.isEmpty() ? bestillingProgressList.get(0).getOrganisasjonsnummer() : null)
                .id(bestillingId)
                .ferdig(isTrue(bestilling.getFerdig()))
                .feil(bestilling.getFeil())
                .environments(Arrays.asList(bestilling.getMiljoer().split(",")))
                .antallLevert(isTrue(bestilling.getFerdig()) && isBlank(bestilling.getFeil()) ? 1 : 0)
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
                            .status(singletonList(RsOrganisasjonStatusRapport.builder()
                                    .navn("Organisasjon Forvalter")
                                    .id(SystemTyper.ORGANISASJON_FORVALTER)
                                    .statuser(singletonList(RsOrganisasjonStatusRapport.Status.builder()
                                            .melding(bestillingStatus.getOrganisasjonsforvalterStatus())
                                            .orgnummer(bestillingStatus.getOrganisasjonsnummer())
                                            .detaljert(singletonList(RsOrganisasjonStatusRapport.Detaljert.builder()
                                                    .miljo(orgBestilling.getMiljoer())
                                                    .orgnummer(bestillingStatus.getOrganisasjonsnummer())
                                                    .build()))
                                            .build()))
                                    .build()))
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
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public OrganisasjonBestilling saveBestillingToDB(OrganisasjonBestilling bestilling) {

        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
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
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public OrganisasjonBestilling saveBestilling(RsOrganisasjonBestillingStatus status) {

        return saveBestillingToDB(
                OrganisasjonBestilling.builder()
                        .antall(1)
                        .sistOppdatert(now())
                        .miljoer(join(",", status.getEnvironments()))
                        .bestKriterier(toJson(status.getBestilling()))
                        .bruker(brukerService.fetchOrCreateBruker(getUserId()))
                        .build());
    }

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
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
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public void setBestillingFerdig(Long bestillingId) {

        Optional<OrganisasjonBestilling> byId = bestillingRepository.findById(bestillingId);

        byId.ifPresent(bestilling -> {
            bestilling.setFerdig(Boolean.TRUE);
            bestilling.setSistOppdatert(now());
            bestillingRepository.save(bestilling);
        });
    }

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public void slettBestillingByBestillingId(Long bestillingId) {

        bestillingRepository.findById(bestillingId).orElseThrow(() ->
                new HttpClientErrorException(HttpStatus.NOT_FOUND, "Fant ikke noen bestilling med id: " + bestillingId));

        organisasjonNummerService.deleteByBestillingId(bestillingId);
        progressService.deleteByBestillingId(bestillingId);
        bestillingRepository.deleteBestillingWithNoChildren(bestillingId);
    }

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public void slettBestillingByOrgnummer(Long orgnummer) {

        List<Long> bestillinger = organisasjonNummerService.fetchBestillingsIdFromOrganisasjonNummer(orgnummer).stream()
                .map(OrganisasjonNummer::getBestillingId)
                .collect(Collectors.toList());

        organisasjonNummerService.deleteByOrgnummer(orgnummer);
        progressService.deleteByOrgnummer(orgnummer);

        bestillinger.forEach(bestillingRepository::deleteBestillingWithNoChildren);
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