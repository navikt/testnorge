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
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.service.BestillingMalService.getBruker;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@Service
@RequiredArgsConstructor
public class OrganisasjonBestillingMalService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";

    private final OrganisasjonBestillingMalRepository organisasjonBestillingMalRepository;
    private final BrukerService brukerService;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final MapperFacade mapperFacade;
    private final GetUserInfo getUserInfo;

    @Transactional
    public void saveOrganisasjonBestillingMal(OrganisasjonBestilling organisasjonBestilling, String malNavn, Bruker bruker) {

        overskrivDuplikateMalbestillinger(malNavn, bruker);
        organisasjonBestillingMalRepository.save(OrganisasjonBestillingMal.builder()
                .bestKriterier(organisasjonBestilling.getBestKriterier())
                .bruker(bruker)
                .malBestillingNavn(malNavn)
                .miljoer(organisasjonBestilling.getMiljoer())
                .build());
    }

    @Transactional
    public void saveOrganisasjonBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        Bruker bruker = brukerService.fetchOrCreateBruker(getUserId(getUserInfo));

        var organisasjonBestilling = organisasjonBestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(bestillingId + " finnes ikke"));

        overskrivDuplikateMalbestillinger(malNavn, bruker);
        organisasjonBestillingMalRepository.save(OrganisasjonBestillingMal.builder()
                .bestKriterier(organisasjonBestilling.getBestKriterier())
                .bruker(bruker)
                .malBestillingNavn(malNavn)
                .miljoer(organisasjonBestilling.getMiljoer())
                .build());
    }

    public RsOrganisasjonMalBestillingWrapper getOrganisasjonMalBestillinger() {

        var malBestillingWrapper = new RsOrganisasjonMalBestillingWrapper();

        var bestillinger = organisasjonBestillingMalRepository.findMalBestilling();

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

    public RsOrganisasjonMalBestilling getOrganisasjonMalBestillingById(Long id) {

        var bestilling = organisasjonBestillingMalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id + " finnes ikke"));

        return RsOrganisasjonMalBestilling.builder()
                .bestilling(mapperFacade.map(bestilling, RsOrganisasjonBestilling.class))
                .malNavn(bestilling.getMalBestillingNavn())
                .id(bestilling.getId())
                .bruker(mapperFacade.map(nonNull(bestilling.getBruker()) ?
                        bestilling.getBruker() :
                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                .build();
    }


    public List<RsOrganisasjonMalBestilling> getMalbestillingerByNavnAndUser(String brukerId, String malNavn) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        return organisasjonBestillingMalRepository.findByBrukerAndMalBestillingNavn(bruker, malNavn)
                .stream().map(bestilling -> RsOrganisasjonMalBestilling.builder()
                        .malNavn(bestilling.getMalBestillingNavn())
                        .id(bestilling.getId())
                        .bestilling(mapperFacade.map(bestilling, RsOrganisasjonBestilling.class))
                        .build()).toList();
    }

    @Transactional
    public void updateOrganisasjonMalBestillingNavnById(Long id, String nyttMalNavn) {

        organisasjonBestillingMalRepository.updateMalBestillingNavnById(id, nyttMalNavn);
    }

    @Transactional
    public void deleteOrganisasjonMalbestillingById(Long id) {

        organisasjonBestillingMalRepository.deleteById(id);
    }

    void overskrivDuplikateMalbestillinger(String malNavn, Bruker bruker) {

        if (StringUtils.isBlank(malNavn)) {
            return;
        }
        var gamleMalBestillinger = organisasjonBestillingMalRepository.findByBrukerAndMalBestillingNavn(bruker, malNavn);
        gamleMalBestillinger.forEach(malBestilling ->
                organisasjonBestillingMalRepository.deleteById(malBestilling.getId()));
    }
}
