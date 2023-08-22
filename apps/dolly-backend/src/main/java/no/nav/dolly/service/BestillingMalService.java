package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper.RsBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

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
public class BestillingMalService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";

    private final BestillingMalRepository bestillingMalRepository;
    private final BestillingRepository bestillingRepository;
    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;
    private final GetUserInfo getUserInfo;

    public RsMalBestillingWrapper getMalBestillinger() {

        var malBestillingWrapper = new RsMalBestillingWrapper();

        var malBestillinger = IterableUtils.toList(bestillingMalRepository.findAll())
                .stream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream()
                        .map(bestillingMal -> RsMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestillingMal, RsBestilling.class))
                                .malNavn(bestillingMal.getMalNavn())
                                .id(bestillingMal.getId())
                                .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                        bestillingMal.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
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

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        return bestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn)
                .stream()
                .map(bestilling -> RsMalBestilling.builder()
                        .malNavn(bestilling.getMalNavn())
                        .id(bestilling.getId())
                        .bestilling(mapperFacade.map(bestilling, RsBestilling.class))
                        .build())
                .sorted(Comparator.comparing(RsMalBestilling::getMalNavn))
                .toList();
    }

    public RsMalBestillingWrapper getMalbestillingByUser(String brukerId) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        var malBestillinger = bestillingMalRepository.findByBruker(bruker)
                .stream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream()
                        .map(bestillingMal -> RsMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestillingMal, RsBestilling.class))
                                .malNavn(bestillingMal.getMalNavn())
                                .id(bestillingMal.getId())
                                .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                        bestillingMal.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
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

        Bruker bruker = brukerService.fetchOrCreateBruker(getUserId(getUserInfo));

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

    public static String getBruker(Bruker bruker) {

        if (isNull(bruker)) {
            return ANONYM;
        }
        return switch (bruker.getBrukertype()) {
            case AZURE, BANKID -> bruker.getBrukernavn();
            case BASIC -> bruker.getNavIdent();
        };
    }

    void overskrivDuplikateMalbestillinger(String malNavn, Bruker bruker) {

        var gamleMalBestillinger = getMalbestillingByUserAndNavn(bruker.getBrukerId(), malNavn);
        gamleMalBestillinger.forEach(malBestilling ->
                bestillingMalRepository.deleteById(malBestilling.getId()));
    }
}