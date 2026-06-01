package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.dto.DashboardDTO;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BestillingRepository bestillingRepository;

    public Flux<DashboardDTO> getBestillingStatus() {

        return bestillingRepository.findBestillingerOrderBySistOppdatert()
                .groupBy(BestillingerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(fragmentliste ->
                        DashboardDTO.builder()
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
                                .annenFeil(fragmentliste.stream()
                                        .filter(fragment ->
                                                "FEIL".equals(fragment.getAnnenstatus()))
                                        .map(BestillingerFragment::getPersoner)
                                        .reduce(0L, Long::sum))
                                .build())
                .sort(Comparator.comparing(DashboardDTO::getDato).reversed());
    }
}
