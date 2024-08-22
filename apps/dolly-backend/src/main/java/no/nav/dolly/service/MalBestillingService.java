package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING_MAL;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@Service
@Slf4j
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
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public RsMalBestillingWrapper getMalBestillinger() {

        var malBestillingWrapper = new RsMalBestillingWrapper();

        //TODO: SLETT ETTER FIX
        var brukere = brukerService.fetchBrukere();
        brukere.forEach(bruker -> {
                    try {
                        var maler = bestillingMalRepository.findByBruker(bruker);
                        log.info("Fant {} maler for bruker: {}", maler.size(), bruker.getBrukernavn());
                    } catch (Exception e) {
                        log.error("Feil ved henting av malbestillinger for bruker: {}", bruker.getBrukernavn(), e);
                    }
                }
        );

        try {

            var malBestillinger = IterableUtils.toList(bestillingMalRepository.findAll())
                    .stream()
                    .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                    .entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(bestillingMal -> {
                                log.info("Malbestilling: {}", Json.pretty(bestillingMal));
                                try {
                                    if (isNull(bestillingMal.getBestKriterier())) {
                                        return null;
                                    }
                                    return RsMalBestilling.builder()
                                            .bestilling(objectMapper.readTree(bestillingMal.getBestKriterier()))
                                            .malNavn(bestillingMal.getMalNavn())
                                            .miljoer(bestillingMal.getMiljoer())
                                            .id(bestillingMal.getId())
                                            .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                                    bestillingMal.getBruker() :
                                                    Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                            .build();
                                } catch (JsonProcessingException e) {
                                    log.error("Feil ved henting av malbestilling: {}", Json.pretty(bestillingMal), e);
                                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                                }
                            })
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                            .toList()));

            malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
            malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                    .toList());

            return malBestillingWrapper;
        } catch (Exception e) {
            log.error("Feil ved henting av malbestillinger", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
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
                                        .bestilling(objectMapper.readTree(bestillingMal.getBestKriterier()))
                                        .miljoer(bestillingMal.getMiljoer())
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

                    .bestKriterier(formatBestillingKriterier(bestilling.getBestKriterier()))
                    .bruker(bruker)
                    .malNavn(malNavn)
                    .miljoer(bestilling.getMiljoer())
                    .build());
        } else {

            var oppdateEksisterende = eksisterende.getFirst();
            oppdateEksisterende.setBestKriterier(bestilling.getBestKriterier());
            oppdateEksisterende.setMiljoer(bestilling.getMiljoer());
        }

        cacheManager.getCache(CACHE_BESTILLING_MAL).clear();
    }

    @Transactional
    public RsMalBestilling saveBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        var bruker = brukerService.fetchBruker(getUserId(getUserInfo));

        var bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(bestillingId + " finnes ikke"));

        BestillingMal malbestilling;
        var maler = bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn);
        if (maler.isEmpty()) {
            malbestilling = bestillingMalRepository.save(BestillingMal.builder()
                    .bestKriterier(formatBestillingKriterier(bestilling.getBestKriterier()))
                    .bruker(bruker)
                    .malNavn(malNavn)
                    .miljoer(bestilling.getMiljoer())
                    .build());
        } else {
            malbestilling = maler.getFirst();
        }

        return mapperFacade.map(malbestilling, RsMalBestilling.class);
    }

    @Transactional
    public void deleteMalBestillingByID(Long id) {

        bestillingMalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Malbestilling med id %d ble ikke funnet".formatted(id)));
        bestillingMalRepository.deleteById(id);
    }

    @Transactional
    public RsMalBestilling updateMalNavnById(Long id, String nyttMalNavn) {

        bestillingMalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Malbestilling med id %d ble ikke funnet".formatted(id)));

        bestillingMalRepository.updateMalNavnById(id, nyttMalNavn);
        var oppdatertMalBestilling = new AtomicReference<RsMalBestilling>();

        bestillingMalRepository.findById(id)
                .ifPresentOrElse(malBestilling ->
                        oppdatertMalBestilling.set(mapperFacade.map(malBestilling, RsMalBestilling.class)), null);
        return oppdatertMalBestilling.get();
    }

    @Transactional
    public RsMalBestilling createFromIdent(String ident, String name) {

        var bruker = brukerService.fetchBruker(getUserId(getUserInfo));

        var bestillinger = bestillingRepository.findBestillingerByIdent(ident);
        if (bestillinger.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingen bestillinger funnet på ident %s".formatted(ident));
        }

        var aggregertRequest = new RsDollyUtvidetBestilling();

        bestillinger.stream()
                .filter(bestilling -> nonNull(bestilling.getBestKriterier()) &&
                        !EMPTY_JSON.equals(bestilling.getBestKriterier()))
                .filter(bestilling -> isNull(bestilling.getOpprettetFraGruppeId()) &&
                        isNull(bestilling.getGjenopprettetFraIdent()) &&
                        isNull(bestilling.getOpprettetFraId()))
                .forEach(bestilling -> {
                    var dollyBestilling = fromJson(bestilling.getBestKriterier());
                    dollyBestilling.getEnvironments().addAll(toSet(bestilling.getMiljoer()));
                    dollyBestilling.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
                    mapperFacade.map(dollyBestilling, aggregertRequest);
                });

        BestillingMal akkumulertMal;
        var maler = bestillingMalRepository.findByBrukerAndMalNavn(bruker, name);
        if (maler.isEmpty()) {

            akkumulertMal = bestillingMalRepository.save(BestillingMal.builder()
                    .bruker(bruker)
                    .malNavn(name)
                    .miljoer(String.join(",", aggregertRequest.getEnvironments()))
                    .bestKriterier(toJson(aggregertRequest))
                    .build());
        } else {

            akkumulertMal = maler.getFirst();
            akkumulertMal.setMiljoer(String.join(",", aggregertRequest.getEnvironments()));
            akkumulertMal.setBestKriterier(toJson(aggregertRequest));
        }

        return mapperFacade.map(akkumulertMal, RsMalBestilling.class);
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

    private String toJson(RsDollyUtvidetBestilling bestilling) {

        try {
            return objectMapper.writeValueAsString(bestilling);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private RsDollyUtvidetBestilling fromJson(String json) {

        try {
            return objectMapper.readValue(json, RsDollyUtvidetBestilling.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private String formatBestillingKriterier(String bestillingKriterier) {
        return bestillingKriterier.replaceAll("fysiskDokument[^,]*+,", "");
    }

    private static Set<String> toSet(String miljoer) {

        return StringUtils.isNotBlank(miljoer) ?
                Arrays.stream(miljoer.split(","))
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
    }
}
