package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import net.logstash.logback.util.StringUtils;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper.RsOrganisasjonMalBestilling;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonBestillingMalRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class OrganisasjonBestillingMalService {

    private static final String FINNES_IKKE = "Mal med id %d finnes ikke";
    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";

    private final OrganisasjonBestillingMalRepository organisasjonBestillingMalRepository;
    private final BrukerService brukerService;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final MapperFacade mapperFacade;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public Mono<OrganisasjonBestillingMal> saveOrganisasjonBestillingMal(OrganisasjonBestilling bestilling, String malNavn) {

        return slettMalbestillingerMedMatchendeNavn(malNavn, bestilling.getBrukerId())
                .then(Mono.just(OrganisasjonBestillingMal.builder()
                        .bestKriterier(bestilling.getBestKriterier())
                        .brukerId(bestilling.getBrukerId())
                        .malNavn(malNavn)
                        .miljoer(bestilling.getMiljoer())
                        .build()))
                .flatMap(organisasjonBestillingMalRepository::save);
    }

    public Mono<OrganisasjonBestillingMal> saveOrganisasjonBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        return organisasjonBestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(bestillingId))))
                .flatMap(bestilling -> getAuthenticatedUserId.call()
                        .flatMap(brukerService::fetchBruker)
                        .flatMap(bruker -> slettMalbestillingerMedMatchendeNavn(malNavn, bruker.getId())
                                .then(Mono.just(OrganisasjonBestillingMal.builder()
                                        .bestKriterier(bestilling.getBestKriterier())
                                        .bruker(bruker)
                                        .malNavn(malNavn)
                                        .miljoer(bestilling.getMiljoer())
                                        .sistOppdatert(LocalDateTime.now())
                                        .build()))
                                .flatMap(organisasjonBestillingMalRepository::save)));
    }

    public Mono<RsOrganisasjonMalBestillingWrapper> getOrganisasjonMalBestillinger() {

        return brukerService.fetchBrukere()
                .collect(Collectors.toMap(Bruker::getId, bruker -> bruker))
                .flatMap(brukere -> organisasjonBestillingMalRepository.findAll()
                        .collect(Collectors.groupingBy(bestilling -> getBruker(brukere, bestilling.getBrukerId())))
                        .flatMap(maler -> Flux.fromIterable(maler.entrySet())
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                                        .stream()
                                        .map(bestilling1 -> RsOrganisasjonMalBestilling.builder()
                                                .bestilling(mapperFacade.map(bestilling1, RsOrganisasjonBestilling.class))
                                                .malNavn(bestilling1.getMalNavn())
                                                .id(bestilling1.getId())
                                                .bruker(mapperFacade.map(nonNull(bestilling1.getBrukerId()) ?
                                                        brukere.get(bestilling1.getBrukerId()) :
                                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                                .build())
                                        .sorted(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn))
                                        .toList()))
                                .map(malBestillinger -> {
                                    var wrapper = RsOrganisasjonMalBestillingWrapper.builder()
                                            .malbestillinger(new HashMap<>(malBestillinger))
                                            .build();
                                    wrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                                            .flatMap(Collection::stream)
                                            .sorted(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn))
                                            .toList());
                                    return wrapper;
                                })));
    }

    public Mono<RsOrganisasjonMalBestillingWrapper> getMalbestillingerByUser(String brukerId) {

        return brukerService.fetchBruker(brukerId)
                .flatMap(bruker -> organisasjonBestillingMalRepository.findByBrukerId(bruker.getId())
                        .map(bestilling1 -> RsOrganisasjonMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestilling1, RsOrganisasjonBestilling.class))
                                .malNavn(bestilling1.getMalNavn())
                                .id(bestilling1.getId())
                                .bruker(mapperFacade.map(bruker, RsBrukerUtenFavoritter.class))
                                .build())
                        .sort(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn))
                        .collectList()
                        .map(orgBestillinger -> RsOrganisasjonMalBestillingWrapper.builder()
                                .malbestillinger(Map.of(bruker.getBrukernavn(), orgBestillinger))
                                .build()));
    }

    public Mono<OrganisasjonBestillingMal> updateOrganisasjonMalNavnById(Long id, String nyttMalNavn) {

        return organisasjonBestillingMalRepository.updateMalNavnById(id, nyttMalNavn)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(id))));
    }

    public Mono<Void> deleteOrganisasjonMalbestillingById(Long id) {

        return organisasjonBestillingMalRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(id))))
                .then(organisasjonBestillingMalRepository.deleteById(id));
    }

    private Mono<Void> slettMalbestillingerMedMatchendeNavn(String malNavn, Long brukerId) {

        if (StringUtils.isBlank(malNavn)) {
            return Mono.empty();
        }
        return organisasjonBestillingMalRepository.findByBrukerIdAndMalNavn(brukerId, malNavn)
                .flatMap(malBestilling -> organisasjonBestillingMalRepository.deleteById(malBestilling.getId()))
                .collectList()
                .then();
    }

    public static String getBruker(Map<Long, Bruker> brukere, Long brukerId) {

        return nonNull(brukerId) ?
                brukere.get(brukerId).getBrukernavn() :
                ANONYM;
    }
}
