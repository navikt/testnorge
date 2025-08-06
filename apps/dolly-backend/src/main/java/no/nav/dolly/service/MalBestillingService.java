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
import no.nav.dolly.domain.projection.MalBestillingFragment;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingSimple;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingSimple.MalBruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING_MAL;
import static no.nav.dolly.config.CachingConfig.CACHE_LEGACY_BESTILLING_MAL;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String FINNES_IKKE = "Mal med id %d finnes ikke";
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
    private final BrukerRepository brukerRepository;

    @Transactional(readOnly = true)
    public Mono<RsMalBestillingWrapper> getMalBestillinger() {

        return brukerRepository.findAll()
                .collect(Collectors.toMap(Bruker::getId, bruker -> RsBrukerUtenFavoritter.builder()
                        .brukerId(bruker.getBrukerId())
                        .brukernavn(bruker.getBrukernavn())
                        .build()))
                .flatMap(brukere -> bestillingMalRepository.findAll()
                        .map(bestillingMal -> {
                            bestillingMal.setBruker(brukere.get(bestillingMal.getBrukerId()));
                            return bestillingMal;
                        })
                        .collect(Collectors.groupingBy(bestilling -> getBruker(brukere, bestilling.getBrukerId())))
                        .flatMap(maler -> Flux.fromIterable(maler.entrySet())
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                                        .stream()
                                        .map(bestillingMal -> {
                                            try {
                                                return RsMalBestillingUtenFavoritter.builder()
                                                        .bestilling(objectMapper.readTree(bestillingMal.getBestKriterier()))
                                                        .malNavn(bestillingMal.getMalNavn())
                                                        .miljoer(bestillingMal.getMiljoer())
                                                        .id(bestillingMal.getId())
                                                        .bruker(nonNull(bestillingMal.getBruker()) ?
                                                                brukere.get(bestillingMal.getBrukerId()) : RsBrukerUtenFavoritter.builder()
                                                                .brukerId(ANONYM)
                                                                .brukernavn(ANONYM)
                                                                .build())
                                                        .build();
                                            } catch (JsonProcessingException e) {
                                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                                            }
                                        })
                                        .sorted(Comparator.comparing(RsMalBestillingUtenFavoritter::getMalNavn))
                                        .toList())))
                        .map(maler -> RsMalBestillingWrapper.builder()
                                .malbestillinger(maler)
                                .build())
                        .map(wrapper -> {
                            wrapper.getMalbestillinger()
                                    .put(ALLE, wrapper.getMalbestillinger().values().stream()
                                            .flatMap(Collection::stream)
                                            .sorted(Comparator.comparing(RsMalBestillingUtenFavoritter::getMalNavn))
                                            .toList());
                            return wrapper;
                        }));
    }

    @Transactional(readOnly = true)
    public Mono<RsMalBestillingWrapper> getMalbestillingByUser(String brukerId) {

        return brukerService.fetchBruker(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bruker med id %s finnes ikke".formatted(brukerId))))
                .flatMap(bruker -> bestillingMalRepository.findByBrukerId(bruker.getId())
                        .collect(Collectors.groupingBy(malBbestilling -> bruker.getBrukernavn()))
                        .flatMap(maler -> Flux.fromIterable(maler.entrySet())
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
                                        .toList()))
                                .map(malBestilling -> RsMalBestillingWrapper.builder()
                                        .malbestillinger(malBestilling)
                                        .build())));
    }

    public Mono<BestillingMal> saveBestillingMal(Bestilling bestilling, String malNavn, Long brukerId) {

        return bestillingMalRepository.findByBrukerIdAndMalNavn(brukerId, malNavn)
                .doOnNext(bestillingMal -> log.info("bestillingMal {}", bestillingMal))
                .switchIfEmpty(brukerRepository.findById(brukerId)
                        .map(bruker -> BestillingMal.builder()
                                .brukerId(bruker.getId())
                                .malNavn(malNavn)
                                .miljoer(bestilling.getMiljoer())
                                .bestKriterier(formatBestillingKriterier(bestilling.getBestKriterier()))
                                .sistOppdatert(now())
                                .build())
                        .flatMap(bestillingMalRepository::save))
                .next()
                .map(bestillingMal -> {
                    bestillingMal.setBestKriterier(bestilling.getBestKriterier());
                    bestillingMal.setMiljoer(bestilling.getMiljoer());
                    return bestillingMal;
                })
                .flatMap(bestillingMalRepository::save)
                .doFinally(bestillingMal -> {
                    if (nonNull(cacheManager.getCache(CACHE_BESTILLING_MAL))) {
                        cacheManager.getCache(CACHE_BESTILLING_MAL).clear();
                    }
                    if (nonNull(cacheManager.getCache(CACHE_LEGACY_BESTILLING_MAL))) {
                        cacheManager.getCache(CACHE_LEGACY_BESTILLING_MAL).clear();
                    }
                });
    }

    @Transactional
    public Mono<BestillingMal> saveBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        return bestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(bestillingId))))
                .zipWith(getAuthenticatedUserId.call()
                        .flatMap(brukerService::fetchBruker))
                .flatMap(tuple -> bestillingMalRepository.findByBrukerIdAndMalNavn(tuple.getT2().getId(), malNavn)
                        .switchIfEmpty(Mono.just(BestillingMal.builder()
                                        .brukerId(tuple.getT2().getId())
                                        .malNavn(malNavn)
                                        .miljoer(tuple.getT1().getMiljoer())
                                        .bestKriterier(formatBestillingKriterier(tuple.getT1().getBestKriterier()))
                                        .sistOppdatert(now())
                                        .build())
                                .flatMap(bestillingMalRepository::save))
                        .next()
                        .map(bestillingMal -> {
                            bestillingMal.setBestKriterier(formatBestillingKriterier(tuple.getT1().getBestKriterier()));
                            bestillingMal.setMiljoer(tuple.getT1().getMiljoer());
                            bestillingMal.setSistOppdatert(now());
                            return bestillingMal;
                        })
                        .flatMap(bestillingMalRepository::save));
    }

    @Transactional
    public Mono<Void> deleteMalBestillingByID(Long id) {

        return bestillingMalRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(id))))
                .flatMap(bestillingMal -> bestillingMalRepository.deleteById(id));
    }

    @Transactional
    public Mono<BestillingMal> updateMalNavnById(Long id, String nyttMalNavn) {

        return bestillingMalRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(id))))
                .flatMap(bestillingMal -> bestillingMalRepository.updateMalNavnById(id, nyttMalNavn))
                .flatMap(bestillingMalRepository::save);
    }

    @Transactional
    public Mono<BestillingMal> createFromIdent(String ident, String name) {

        var aggregertRequest = new RsDollyUtvidetBestilling();

        return bestillingRepository.findBestillingerByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException("Ingen bestillinger funnet for ident %s".formatted(ident))))
                .filter(bestilling -> nonNull(bestilling.getBestKriterier()) &&
                        !EMPTY_JSON.equals(bestilling.getBestKriterier()))
                .filter(bestilling -> isNull(bestilling.getOpprettetFraGruppeId()) &&
                        isNull(bestilling.getGjenopprettetFraIdent()) &&
                        isNull(bestilling.getOpprettetFraId()))
                .map(bestilling -> {
                    var dollyBestilling = fromJson(bestilling.getBestKriterier());
                    dollyBestilling.getEnvironments().addAll(toSet(bestilling.getMiljoer()));
                    dollyBestilling.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
                    mapperFacade.map(dollyBestilling, aggregertRequest);
                    return aggregertRequest;
                })
                .collectList()
                .then(getAuthenticatedUserId.call()
                        .flatMap(brukerService::fetchBruker))
                .flatMap(bruker -> bestillingMalRepository.findByBrukerIdAndMalNavn(bruker.getId(), name)
                        .switchIfEmpty(Mono.just(BestillingMal.builder()
                                        .brukerId(bruker.getId())
                                        .malNavn(name)
                                        .miljoer(String.join(",", aggregertRequest.getEnvironments()))
                                        .bestKriterier(toJson(aggregertRequest))
                                        .sistOppdatert(now())
                                        .build())
                                .flatMap(bestillingMalRepository::save))
                        .next()
                        .map(bestillingMal -> {
                            bestillingMal.setBestKriterier(toJson(aggregertRequest));
                            bestillingMal.setMiljoer(String.join(",", aggregertRequest.getEnvironments()));
                            bestillingMal.setSistOppdatert(now());
                            return bestillingMal;
                        })
                        .flatMap(bestillingMalRepository::save));
    }

    public static String getBruker(Map<Long, RsBrukerUtenFavoritter> brukere, Long brukerId) {

        return nonNull(brukerId) ?
                brukere.get(brukerId).getBrukernavn() :
                ANONYM;
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

        return getAuthenticatedUserId.call()
                .flatMap(brukerService::fetchBruker)
                .flatMap(bruker -> {
                    if (bruker.getBrukertype() == AZURE) {
                        return bestillingMalRepository.findAllByBrukertypeAzure()
                                .collectList()
                                .flatMap(bestillingMalFragments -> Mono.just(RsMalBestillingSimple.builder()
                                        .brukereMedMaler(Stream.of(List.of(
                                                                MalBruker.builder()
                                                                        .brukernavn(ALLE)
                                                                        .brukerId(ALLE)
                                                                        .build(),
                                                                MalBruker.builder()
                                                                        .brukernavn(ANONYM)
                                                                        .brukerId(ANONYM)
                                                                        .build()),
                                                        mapFragment(bestillingMalFragments))
                                                .flatMap(List::stream)
                                                .toList())
                                        .build()));
                    } else {

                        return brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                .map(TilgangDTO::getBrukere)
                                .map(bestillingMalRepository::findAllByBrukerIdIn)
                                .flatMap(Flux::collectList)
                                .map(MalBestillingService::mapFragment)
                                .map(RsMalBestillingSimple::new);
                    }
                });
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

    public Flux<RsMalBestilling> getMalBestillingerBrukerId(String brukerId) {

        return Flux.just(switch (brukerId) {
                    case ANONYM -> bestillingMalRepository.findAllByBrukerIsNull();
                    case ALLE -> bestillingMalRepository.findAllByBrukerAzure();
                    default -> bestillingMalRepository.findAllByBrukerId(brukerId);
                })
                .flatMap(Flux::from)
                .map(bestillingMal -> mapperFacade.map(bestillingMal, RsMalBestilling.class));
    }
}