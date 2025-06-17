package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.MalBestillingFragment;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingSimple;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingSimple.MalBruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING_MAL;
import static no.nav.dolly.config.CachingConfig.CACHE_LEGACY_BESTILLING_MAL;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";
    private static final String EMPTY_JSON = "{}";

    private final BestillingMalRepository bestillingMalRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final BestillingRepository bestillingRepository;
    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

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
                                return RsMalBestillingUtenFavoritter.builder()
                                        .bestilling(objectMapper.readTree(bestillingMal.getBestKriterier()))
                                        .malNavn(bestillingMal.getMalNavn())
                                        .miljoer(bestillingMal.getMiljoer())
                                        .id(bestillingMal.getId())
                                        .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                                bestillingMal.getBruker() :
                                                Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                        .build();
                            } catch (JsonProcessingException e) {
                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                            }
                        })
                        .sorted(Comparator.comparing(RsMalBestillingUtenFavoritter::getMalNavn))
                        .toList()));

        malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
        malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(RsMalBestillingUtenFavoritter::getMalNavn))
                .toList());

        return malBestillingWrapper;
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
                                return RsMalBestillingUtenFavoritter.builder()
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
                        .sorted(Comparator.comparing(RsMalBestillingUtenFavoritter::getMalNavn))
                        .toList()));

        return RsMalBestillingWrapper.builder()
                .malbestillinger(malBestillinger)
                .build();
    }

    public Mono<BestillingMal> saveBestillingMal(Bestilling bestilling, String malNavn, Bruker bruker) {

        var eksisterende = bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn);

        if (eksisterende.isEmpty()) {
            bestillingMalRepository.save(BestillingMal.builder()

                    .bestKriterier(formatBestillingKriterier(bestilling.getBestKriterier()))
                    .bruker(bruker)
                    .malNavn(malNavn)
                    .miljoer(bestilling.getMiljoer())
                    .sistOppdatert(LocalDateTime.now())
                    .build());
        } else {

            var oppdateEksisterende = eksisterende.getFirst();
            oppdateEksisterende.setBestKriterier(bestilling.getBestKriterier());
            oppdateEksisterende.setMiljoer(bestilling.getMiljoer());
        }

        if (nonNull(cacheManager.getCache(CACHE_BESTILLING_MAL))) {
            cacheManager.getCache(CACHE_BESTILLING_MAL).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_LEGACY_BESTILLING_MAL))) {
            cacheManager.getCache(CACHE_LEGACY_BESTILLING_MAL).clear();
        }
    }

    @Transactional
    public Mono<RsMalBestillingUtenFavoritter> saveBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        var bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(bestillingId + " finnes ikke"));

        return getAuthenticatedUserId.call()
                .map(brukerService::fetchBruker)
                .map(bruker -> Mono.just(bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn))
                        .map(maler -> maler.isEmpty() ?
                                bestillingMalRepository.save(BestillingMal.builder()
                                        .bestKriterier(formatBestillingKriterier(bestilling.getBestKriterier()))
                                        .bruker(bruker)
                                        .malNavn(malNavn)
                                        .miljoer(bestilling.getMiljoer())
                                        .sistOppdatert(LocalDateTime.now())
                                        .build()) :
                                maler.getFirst()))
                .flatMap(Mono::from)
                .map(malbestilling -> mapperFacade.map(malbestilling, RsMalBestillingUtenFavoritter.class));
//
//
//
//        BestillingMal malbestilling;
//        var maler = bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn);
//        if (maler.isEmpty()) {
//            malbestilling = bestillingMalRepository.save(BestillingMal.builder()
//                    .bestKriterier(formatBestillingKriterier(bestilling.getBestKriterier()))
//                    .bruker(bruker)
//                    .malNavn(malNavn)
//                    .miljoer(bestilling.getMiljoer())
//                    .sistOppdatert(LocalDateTime.now())
//                    .build());
//        } else {
//            malbestilling = maler.getFirst();
//        }
//
//        return mapperFacade.map(malbestilling, RsMalBestillingUtenFavoritter.class);
    }

    @Transactional
    public void deleteMalBestillingByID(Long id) {

        bestillingMalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Malbestilling med id %d ble ikke funnet".formatted(id)));
        bestillingMalRepository.deleteById(id);
    }

    @Transactional
    public RsMalBestillingUtenFavoritter updateMalNavnById(Long id, String nyttMalNavn) {

        bestillingMalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Malbestilling med id %d ble ikke funnet".formatted(id)));

        bestillingMalRepository.updateMalNavnById(id, nyttMalNavn);
        var oppdatertMalBestilling = new AtomicReference<RsMalBestillingUtenFavoritter>();

        bestillingMalRepository.findById(id)
                .ifPresentOrElse(malBestilling ->
                        oppdatertMalBestilling.set(mapperFacade.map(malBestilling, RsMalBestillingUtenFavoritter.class)), null);
        return oppdatertMalBestilling.get();
    }

    @Transactional
    public Mono<RsMalBestillingUtenFavoritter> createFromIdent(String ident, String name) {

        var bestillinger = bestillingRepository.findBestillingerByIdent(ident);
        if (bestillinger.isEmpty()) {
            throw new NotFoundException("Ingen bestillinger funnet på ident %s".formatted(ident));
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

        return getAuthenticatedUserId.call()
                .map(brukerService::fetchBruker)
                .map(bruker -> Mono.just(bestillingMalRepository.findByBrukerAndMalNavn(bruker, name))
                        .map(maler -> maler.isEmpty() ?
                                bestillingMalRepository.save(BestillingMal.builder()
                                        .bruker(bruker)
                                        .malNavn(name)
                                        .miljoer(String.join(",", aggregertRequest.getEnvironments()))
                                        .bestKriterier(toJson(aggregertRequest))
                                        .build()) :
                                Mono.just(maler.getFirst())
                                        .map(mal -> BestillingMal.builder()
                                                .bruker(mal.getBruker())
                                                .malNavn(mal.getMalNavn())
                                                .miljoer(String.join(",", aggregertRequest.getEnvironments()))
                                                .bestKriterier(toJson(aggregertRequest))
                                                .build())))
                .flatMap(Mono::from)
                .map(mal -> mapperFacade.map(mal, RsMalBestillingUtenFavoritter.class));
    }

    public static String getBruker(Bruker bruker) {

        if (isNull(bruker)) {
            return ANONYM;

        } else {
            return bruker.getBrukernavn();
        }
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

    public Mono<RsMalBestillingSimple> getMalBestillingOversikt() {

        var brukeren = brukerService.fetchOrCreateBruker();
//        if (brukeren.getBrukertype() == AZURE) {
//
//            return Mono.just(RsMalBestillingSimple.builder()
//                    .brukereMedMaler(Stream.of(List.of(
//                                            MalBruker.builder()
//                                                    .brukernavn(ALLE)
                                                    .brukerId(ALLE)
                                                    .build(),
                                            MalBruker.builder()
                                                    .brukernavn(ANONYM)
//                                                    .brukerId(ANONYM)
//                                                    .build()),
//                                    mapFragment(bestillingMalRepository.findAllByBrukertypeAzure()))
//                            .flatMap(List::stream)
//                            .toList())
//                    .build());
//
//        } else {
//
//            return brukerServiceConsumer.getKollegaerIOrganisasjon(brukeren.getBrukerId())
//                    .map(TilgangDTO::getBrukere)
//                    .map(bestillingMalRepository::findAllByBrukerIdIn)
//                    .map(MalBestillingService::mapFragment)
//                    .map(RsMalBestillingSimple::new);
//        }
        return Mono.empty(); // TBD
    }

    private static List<MalBruker> mapFragment(List<MalBestillingFragment> malBestillingFragment) {

        return malBestillingFragment.stream()
                .map(MalBestillingFragment::getMalBruker)
                .filter(Objects::nonNull)
                .map(malBruker -> malBruker.split(":"))
                .map(malBruker -> MalBruker.builder()
                        .brukernavn(malBruker[0])
                        .brukerId(malBruker[1])
                        .build())
                .toList();
    }

    public List<RsMalBestilling> getMalBestillingerBrukerId(String brukerId) {

        var malBestillinger =  switch (brukerId) {
            case ANONYM -> bestillingMalRepository.findAllByBrukerIsNull();
            case ALLE -> bestillingMalRepository.findAllByBrukerAzure();
            default -> bestillingMalRepository.findAllByBrukerId(brukerId);
        };

        return mapperFacade.mapAsList(malBestillinger, RsMalBestilling.class);
    }
}