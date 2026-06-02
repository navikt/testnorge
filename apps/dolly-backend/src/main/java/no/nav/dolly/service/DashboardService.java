package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.dto.DashboardPersonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BestillingRepository bestillingRepository;

    public Flux<DashboardPersonerDTO> getPersonerStatus() {

        return bestillingRepository.findBestillingerOrderBySistOppdatert()
                .groupBy(BestillingerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(fragmentliste ->
                        DashboardPersonerDTO.builder()
                                .dato(fragmentliste.stream()
                                        .map(BestillingerFragment::getDato)
                                        .findAny().orElse(null))
                                .personerTotalt(fragmentliste.stream()
                                        .map(BestillingerFragment::getPersoner)
                                        .reduce(0L, Long::sum))
                                .nye(fragmentliste.stream()
                                        .filter(fragment -> "NYBESTILLING".equals(fragment.getGjenopprettstatus()))
                                        .map(BestillingerFragment::getPersoner)
                                        .reduce(0L, Long::sum))
                                .gjenopprettede(fragmentliste.stream()
                                        .filter(fragment -> "GJENOPPRETTING".equals(fragment.getGjenopprettstatus()))
                                        .map(BestillingerFragment::getPersoner)
                                        .reduce(0L, Long::sum))
                                .pdlFeil(fragmentliste.stream()
                                        .filter(fragment ->
                                                "FEIL".equals(fragment.getPdlstatus()))
                                        .map(BestillingerFragment::getPersoner)
                                        .reduce(0L, Long::sum))
                                .andreFeil(fragmentliste.stream()
                                        .filter(fragment ->
                                                "FEIL".equals(fragment.getAnnenstatus()))
                                        .map(BestillingerFragment::getPersoner)
                                        .reduce(0L, Long::sum))
                                .build())
                .sort(Comparator.comparing(DashboardPersonerDTO::getDato).reversed());
    }


    public Flux<DashboardTeamsDTO> getTeamsStatus() {

        return bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert()
                .groupBy(TeamFragment::getDato)
                .flatMap(Flux::collectList)
                .map(fragmentliste ->
                        DashboardTeamsDTO.builder()
                                .dato(fragmentliste.stream()
                                        .map(TeamFragment::getDato)
                                        .findAny().orElse(null))
                                .entries(fragmentliste.stream()
                                        .filter(fragment -> isNotBlank(fragment.getEpost()))
                                        .map(fragment -> new DashboardTeamsDTO.Entry(
                                                fragment.getEpost(),
                                                fragment.getAntall()))
                                        .toList())
                                .build())
                .sort(Comparator.comparing(DashboardTeamsDTO::getDato).reversed());
    }
}
