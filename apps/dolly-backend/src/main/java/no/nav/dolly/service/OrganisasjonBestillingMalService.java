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
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.service.MalBestillingService.getBruker;

@Service
@RequiredArgsConstructor
public class OrganisasjonBestillingMalService {

    private static final String ANONYM = "FELLES";
    private static final String ALLE = "ALLE";

    private final OrganisasjonBestillingMalRepository organisasjonBestillingMalRepository;
    private final BrukerService brukerService;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final MapperFacade mapperFacade;

    public void saveOrganisasjonBestillingMal(OrganisasjonBestilling organisasjonBestilling, String malNavn, Bruker bruker) {

        overskrivDuplikateMalbestillinger(malNavn, bruker);
        organisasjonBestillingMalRepository.save(OrganisasjonBestillingMal.builder()
                .bestKriterier(organisasjonBestilling.getBestKriterier())
                .bruker(bruker)
                .malNavn(malNavn)
                .miljoer(organisasjonBestilling.getMiljoer())
                .build());
    }

    public void saveOrganisasjonBestillingMalFromBestillingId(Long bestillingId, String malNavn) {

        Bruker bruker = brukerService.fetchOrCreateBruker();

        var organisasjonBestilling = organisasjonBestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(bestillingId + " finnes ikke"));

        overskrivDuplikateMalbestillinger(malNavn, bruker);
        organisasjonBestillingMalRepository.save(OrganisasjonBestillingMal.builder()
                .bestKriterier(organisasjonBestilling.getBestKriterier())
                .bruker(bruker)
                .malNavn(malNavn)
                .miljoer(organisasjonBestilling.getMiljoer())
                .build());
    }

    public RsOrganisasjonMalBestillingWrapper getOrganisasjonMalBestillinger() {

        var malBestillingWrapper = new RsOrganisasjonMalBestillingWrapper();

        var bestillinger = IterableUtils.toList(organisasjonBestillingMalRepository.findAll());

        var malBestillinger = bestillinger.parallelStream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(bestilling1 -> RsOrganisasjonMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestilling1, RsOrganisasjonBestilling.class))
                                .malNavn(bestilling1.getMalNavn())
                                .id(bestilling1.getId())
                                .bruker(mapperFacade.map(nonNull(bestilling1.getBruker()) ?
                                        bestilling1.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
                        .sorted(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn))
                        .toList()));

        malBestillingWrapper.getMalbestillinger().putAll(malBestillinger);
        malBestillingWrapper.getMalbestillinger().put(ALLE, malBestillinger.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn))
                .toList());

        return malBestillingWrapper;
    }

    public RsOrganisasjonMalBestillingWrapper getMalbestillingerByUser(String brukerId) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        var malBestillinger = organisasjonBestillingMalRepository.findByBruker(bruker).parallelStream()
                .collect(Collectors.groupingBy(bestilling -> getBruker(bestilling.getBruker())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(bestilling1 -> RsOrganisasjonMalBestilling.builder()
                                .bestilling(mapperFacade.map(bestilling1, RsOrganisasjonBestilling.class))
                                .malNavn(bestilling1.getMalNavn())
                                .id(bestilling1.getId())
                                .bruker(mapperFacade.map(nonNull(bestilling1.getBruker()) ?
                                        bestilling1.getBruker() :
                                        Bruker.builder().brukerId(ANONYM).brukernavn(ANONYM).build(), RsBrukerUtenFavoritter.class))
                                .build())
                        .sorted(Comparator.comparing(RsOrganisasjonMalBestilling::getMalNavn))
                        .toList()));

        return RsOrganisasjonMalBestillingWrapper.builder()
                .malbestillinger(malBestillinger)
                .build();
    }

    public int updateOrganisasjonMalNavnById(Long id, String nyttMalNavn) {

        return organisasjonBestillingMalRepository.updateMalNavnById(id, nyttMalNavn);
    }

    public void deleteOrganisasjonMalbestillingById(Long id) {

        organisasjonBestillingMalRepository.deleteById(id);
    }

    void overskrivDuplikateMalbestillinger(String malNavn, Bruker bruker) {

        if (StringUtils.isBlank(malNavn)) {
            return;
        }
        var gamleMalBestillinger = organisasjonBestillingMalRepository.findByBrukerAndMalNavn(bruker, malNavn);
        gamleMalBestillinger.forEach(malBestilling ->
                organisasjonBestillingMalRepository.deleteById(malBestilling.getId()));
    }
}
