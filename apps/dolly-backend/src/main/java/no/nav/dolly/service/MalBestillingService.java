package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@Service
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";
    private static final String EMPTY_JSON = "{}";

    private final BestillingMalRepository bestillingMalRepository;
    private final BestillingRepository bestillingRepository;
    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;
    private final GetUserInfo getUserInfo;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public RsMalBestillingWrapper getMalBestillinger() {

        var malBestillingWrapper = new RsMalBestillingWrapper();

        var malBestillinger = IterableUtils.toList(bestillingMalRepository.findAll())
                .stream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream()
                        .map(bestillingMal -> {
                            try {
                                return RsMalBestilling.builder()
                                        .bestKriterier(objectMapper.readTree(bestillingMal.getBestKriterier()))
                                        .malNavn(bestillingMal.getMalNavn())
                                        .id(bestillingMal.getId())
                                        .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                                bestillingMal.getBruker() :
                                                Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                        .build();
                            } catch (JsonProcessingException e) {
                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                            }
                        })
                        .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                        .toList()));

        malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
        malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                .toList());

        return malBestillingWrapper;
    }

    public List<RsMalBestilling> getMalbestillingByUserAndNavn(String brukerId, String malNavn) {

        var bruker = brukerService.fetchBruker(brukerId);

        return bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn)
                .stream()
                .map(bestilling -> {
                    try {
                        return RsMalBestilling.builder()
                                .id(bestilling.getId())
                                .bruker(mapperFacade.map(bruker, RsBrukerUtenFavoritter.class))
                                .malNavn(bestilling.getMalNavn())
                                .bestKriterier(objectMapper.readTree(bestilling.getBestKriterier()))
                                .sistOppdatert(bestilling.getSistOppdatert())
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                })
                .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                .toList();
    }

    @Transactional(readOnly = true)
    public RsMalBestillingWrapper getMalbestillingByUser(String brukerId) {

        var bruker = brukerService.fetchBruker(brukerId);

        var malBestillinger = bestillingMalRepository.findByBruker(bruker)
                .stream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream()
                        .map(bestillingMal -> {
                            try {
                                return RsMalBestilling.builder()
                                        .bestKriterier(objectMapper.readTree(bestillingMal.getBestKriterier()))
                                        .malNavn(bestillingMal.getMalNavn())
                                        .id(bestillingMal.getId())
                                        .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                                bestillingMal.getBruker() :
                                                Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                        .build();
                            } catch (JsonProcessingException e) {
                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
                            }
                        })
                        .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                        .toList()));

        return RsMalBestillingWrapper.builder()
                .malbestillinger(malBestillinger)
                .build();
    }

    public void saveBestillingMal(Bestilling bestilling, String malNavn, Bruker bruker) {

        var eksisterende = bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn);
        if (eksisterende.isEmpty()) {
            bestillingMalRepository.save(BestillingMal.builder()

                    .bestKriterier(bestilling.getBestKriterier())
                    .bruker(bruker)
                    .malNavn(malNavn)
                    .miljoer(bestilling.getMiljoer())
                    .build());
        } else {
            eksisterende.stream()
                    .findFirst()
                    .ifPresent(malbestilling -> {
                        malbestilling.setBestKriterier(bestilling.getBestKriterier());
                        malbestilling.setMiljoer(bestilling.getMiljoer());
                    });
        }
    }

    public void saveBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        var bruker = brukerService.fetchOrCreateBruker(getUserId(getUserInfo));

        var bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(bestillingId + " finnes ikke"));

        overskrivDuplikateMalbestillinger(malNavn, bruker);
        bestillingMalRepository.save(BestillingMal.builder()
                .bestKriterier(bestilling.getBestKriterier())
                .bruker(bruker)
                .malNavn(malNavn)
                .miljoer(bestilling.getMiljoer())
                .build());
    }

    public void deleteMalBestillingByID(Long id) {

        bestillingMalRepository.deleteById(id);
    }

    public void updateMalNavnById(Long id, String nyttMalNavn) {

        bestillingMalRepository.updateMalNavnById(id, nyttMalNavn);
    }

    @SneakyThrows
    @Transactional
    public RsMalBestilling createFromIdent(String ident, String name) {

        var bruker = brukerService.fetchBruker(getUserId(getUserInfo));

        var bestillinger = bestillingRepository.findBestillingerByIdent(ident);
        if (bestillinger.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingen bestillinger funnet på ident %s".formatted(ident));
        }
        var aggregertRequest = new RsDollyBestillingRequest();

        bestillinger.stream()
                .filter(bestilling -> nonNull(bestilling.getBestKriterier()) &&
                        !EMPTY_JSON.equals(bestilling.getBestKriterier()))
                .filter(bestilling -> isNull(bestilling.getOpprettetFraGruppeId()) &&
                        isNull(bestilling.getGjenopprettetFraIdent()) &&
                        isNull(bestilling.getOpprettetFraId()))
                .map(bestilling -> {
                    try {
                        return objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
                    }
                })
                .forEach(dollyBestilling -> mapperFacade.map(dollyBestilling, aggregertRequest));

        var akkumulertMal = bestillingMalRepository.save(BestillingMal.builder()
                .bruker(bruker)
                .malNavn(name)
                .miljoer(String.join(",", aggregertRequest.getEnvironments()))
                .bestKriterier(objectMapper.writeValueAsString(aggregertRequest))
                .build());

        return RsMalBestilling.builder()
                .id(akkumulertMal.getId())
                .bruker(mapperFacade.map(bruker, RsBrukerUtenFavoritter.class))
                .malNavn(akkumulertMal.getMalNavn())
                .miljoer(akkumulertMal.getMiljoer())
                .bestKriterier(objectMapper.readTree(akkumulertMal.getBestKriterier()))
                .sistOppdatert(akkumulertMal.getSistOppdatert())
                .build();
    }

    public static String getBruker(Bruker bruker) {

        if (isNull(bruker)) {
            return ANONYM;
        }
        return switch (bruker.getBrukertype()) {
            case AZURE, BANKID -> bruker.getBrukernavn();
            case BASIC -> bruker.getNavIdent();
        };
    }

    private void overskrivDuplikateMalbestillinger(String malNavn, Bruker bruker) {

        var gamleMalBestillinger = getMalbestillingByUserAndNavn(bruker.getBrukerId(), malNavn);
        gamleMalBestillinger.forEach(malBestilling ->
                bestillingMalRepository.deleteById(malBestilling.getId()));
    }
}