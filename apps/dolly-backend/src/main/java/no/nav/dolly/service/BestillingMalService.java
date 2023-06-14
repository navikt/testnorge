package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper.RsOrganisasjonMalBestilling;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.OrganisasjonBestillingMalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.logstash.logback.util.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class BestillingMalService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";

    private final BestillingMalRepository bestillingMalRepository;
    private final OrganisasjonBestillingMalRepository organisasjonBestillingMalRepository;
    private final BrukerService brukerService;
    private final OrganisasjonBestillingService organisasjonBestillingService;
    private final MapperFacade mapperFacade;

    public RsMalBestillingWrapper getMalBestillinger() {

        RsMalBestillingWrapper malBestillingWrapper = new RsMalBestillingWrapper();

        var malBestillinger = bestillingMalRepository.findMalBestilling()
                .stream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(bestilling1 -> RsMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestilling1, RsMalBestillingWrapper.RsBestilling.class))
                                .malNavn(bestilling1.getMalBestillingNavn())
                                .id(bestilling1.getId())
                                .bruker(mapperFacade.map(nonNull(bestilling1.getBruker()) ?
                                        bestilling1.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
                        .toList()));

        malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
        malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(RsMalBestilling::getMalNavn)
                        .thenComparing(RsMalBestilling::getId))
                .toList());

        return malBestillingWrapper;
    }

    public List<RsMalBestilling> getMalbestillingByNavnAndUser(String brukerId, String malNavn) {

        Bruker bruker = brukerService.fetchBruker(brukerId);

        var bestillinger = nonNull(malNavn)
                ? bestillingMalRepository.findByBrukerAndMalBestillingNavn(bruker, malNavn)
                : bestillingMalRepository.findByBruker(bruker);

        return bestillinger.stream().map(bestilling -> RsMalBestilling.builder()
                .malNavn(bestilling.getMalBestillingNavn())
                .id(bestilling.getId())
                .bestilling(mapperFacade.map(bestilling, RsMalBestillingWrapper.RsBestilling.class))
                .build()).toList();
    }

    public RsOrganisasjonMalBestillingWrapper getOrganisasjonMalBestillinger() {

        RsOrganisasjonMalBestillingWrapper malBestillingWrapper = new RsOrganisasjonMalBestillingWrapper();

        List<OrganisasjonBestillingMal> bestillinger = organisasjonBestillingMalRepository.findMalBestilling();

        var malBestillinger = bestillinger.parallelStream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(bestilling1 -> RsOrganisasjonMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestilling1, RsOrganisasjonBestilling.class))
                                .malNavn(bestilling1.getMalBestillingNavn())
                                .id(bestilling1.getId())
                                .bruker(mapperFacade.map(nonNull(bestilling1.getBruker()) ?
                                        bestilling1.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
                        .toList()));

        malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
        malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn)
                        .thenComparing(RsOrganisasjonMalBestilling::getId))
                .toList());

        return malBestillingWrapper;
    }


    @Transactional
    public void saveBestillingMal(Bestilling bestilling, Bruker bruker) {

        bestillingMalRepository.save(BestillingMal.builder()
                .bestKriterier(bestilling.getBestKriterier())
                .bruker(bruker)
                .malBestillingNavn(bestilling.getMalBestillingNavn())
                .miljoer(bestilling.getMiljoer())
                .build());
    }

    @Transactional
    public void saveOrganisasjonBestillingMal(OrganisasjonBestilling bestilling, Bruker bruker) {

        organisasjonBestillingMalRepository.save(OrganisasjonBestillingMal.builder()
                .bestKriterier(bestilling.getBestKriterier())
                .bruker(bruker)
                .malBestillingNavn(bestilling.getMalBestillingNavn())
                .miljoer(bestilling.getMiljoer())
                .build());
    }

    public List<RsOrganisasjonMalBestilling> getOrganisasjonMalbestillingByNavnAndUser(String brukerId, String malNavn) {

        List<OrganisasjonBestilling> bestillinger = organisasjonBestillingService.fetchMalbestillingByNavnAndUser(brukerId, malNavn);
        return bestillinger.stream().map(bestilling -> RsOrganisasjonMalBestilling.builder()
                .malNavn(bestilling.getMalBestillingNavn())
                .bestilling(mapperFacade.map(bestilling, RsOrganisasjonBestilling.class))
                .id(bestilling.getId())
                .build()).toList();
    }

    @Transactional
    public void deleteMalBestillingByID(Long id) {

        bestillingMalRepository.deleteById(id);
    }

    @Transactional
    public void updateMalBestillingNavnById(Long id, String nyttMalNavn) {

        bestillingMalRepository.updateMalBestillingNavnById(id, nyttMalNavn);
    }

    @Transactional
    public void updateOrganisasjonMalBestillingNavnById(Long id, String nyttMalNavn) {

        organisasjonBestillingMalRepository.updateMalBestillingNavnById(id, nyttMalNavn);
    }

    private static String getBruker(Bruker bruker) {

        if (isNull(bruker)) {
            return ANONYM;
        }
        return switch (bruker.getBrukertype()) {
            case AZURE, BANKID -> bruker.getBrukernavn();
            case BASIC -> bruker.getNavIdent();
        };
    }

    void overskrivDuplikateMalbestillinger(Bestilling bestilling) {

        if (isBlank(bestilling.getMalBestillingNavn())) {
            return;
        }
        var gamleMalBestillinger = getMalbestillingByNavnAndUser(bestilling.getBruker().getBrukerId(), bestilling.getMalBestillingNavn());
        gamleMalBestillinger.forEach(malBestilling ->
                bestillingMalRepository.deleteById(malBestilling.getId()));
    }
}
