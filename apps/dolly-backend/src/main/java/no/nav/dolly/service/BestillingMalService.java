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

        var malBestillinger = bestillingMalRepository.findMalBestilling()
                .stream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(bestillingMal -> RsMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestillingMal, RsBestilling.class))
                                .malNavn(bestillingMal.getMalBestillingNavn())
                                .id(bestillingMal.getId())
                                .bruker(mapperFacade.map(nonNull(bestillingMal.getBruker()) ?
                                        bestillingMal.getBruker() :
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

    public RsMalBestilling getMalBestillingById(Long id) {

        var malBestilling = bestillingMalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id + " finnes ikke"));

        return RsMalBestilling.builder()
                .bestilling(mapperFacade.map(malBestilling, RsBestilling.class))
                .malNavn(malBestilling.getMalBestillingNavn())
                .id(malBestilling.getId())
                .bruker(mapperFacade.map(nonNull(malBestilling.getBruker()) ?
                        malBestilling.getBruker() :
                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                .build();
    }

    public List<RsMalBestilling> getMalbestillingByUserAndNavn(String brukerId, String malNavn) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        return bestillingMalRepository.findByBrukerAndMalBestillingNavn(bruker, malNavn)
                .stream().map(bestilling -> RsMalBestilling.builder()
                        .malNavn(bestilling.getMalBestillingNavn())
                        .id(bestilling.getId())
                        .bestilling(mapperFacade.map(bestilling, RsBestilling.class))
                        .build()).toList();
    }

    public List<RsMalBestilling> getMalbestillingByUser(String brukerId) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        return bestillingMalRepository.findByBruker(bruker)
                .stream().map(bestilling -> RsMalBestilling.builder()
                        .malNavn(bestilling.getMalBestillingNavn())
                        .id(bestilling.getId())
                        .bestilling(mapperFacade.map(bestilling, RsBestilling.class))
                        .build()).toList();
    }


    @Transactional
    public void saveBestillingMal(Bestilling bestilling, String malnavn, Bruker bruker) {

        bestillingMalRepository.save(BestillingMal.builder()
                .bestKriterier(bestilling.getBestKriterier())
                .bruker(bruker)
                .malBestillingNavn(malnavn)
                .miljoer(bestilling.getMiljoer())
                .build());
    }

    @Transactional
    public void saveBestillingMalFromBestillingId(Long bestillingId, String malnavn) {

        Bruker bruker = brukerService.fetchOrCreateBruker(getUserId(getUserInfo));

        var bestilling = bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(bestillingId + " finnes ikke"));

        overskrivDuplikateMalbestillinger(malnavn, bruker);
        bestillingMalRepository.save(BestillingMal.builder()
                .bestKriterier(bestilling.getBestKriterier())
                .bruker(bruker)
                .malBestillingNavn(malnavn)
                .miljoer(bestilling.getMiljoer())
                .build());
    }

    @Transactional
    public void deleteMalBestillingByID(Long id) {

        bestillingMalRepository.deleteById(id);
    }

    @Transactional
    public void updateMalBestillingNavnById(Long id, String nyttMalNavn) {

        bestillingMalRepository.updateMalBestillingNavnById(id, nyttMalNavn);
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

    void overskrivDuplikateMalbestillinger(String malnavn, Bruker bruker) {

        if (isBlank(malnavn)) {
            return;
        }
        var gamleMalBestillinger = getMalbestillingByUserAndNavn(bruker.getBrukerId(), malnavn);
        gamleMalBestillinger.forEach(malBestilling ->
                bestillingMalRepository.deleteById(malBestilling.getId()));
    }
}