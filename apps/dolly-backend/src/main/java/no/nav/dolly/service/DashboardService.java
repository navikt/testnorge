package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.teamkatalog.TeamkatalogConsumer;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.domain.dto.DashboardPersonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BestillingRepository bestillingRepository;
    private final BrukerRepository brukerRepository;
    private final TeamkatalogConsumer teamkatalogConsumer;

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

        return brukerRepository.findAll()
                .filter(bruker -> isNotBlank(bruker.getEpost()))
                .map(Bruker::getEpost)
                .distinct()
                .buffer(100)
                .flatMap(teamkatalogConsumer::getTeamForEpost, 5)
                .collect(Collectors.toMap(TeamkatalogDTO::getEmail, TeamkatalogDTO::getTeamNavn))
                .flatMapMany(teams -> bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert()
                        .groupBy(TeamFragment::getDato)
                        .flatMap(Flux::collectList)
                        .flatMap(teamfragmentDato -> {

                            var groupedTeam = new HashMap<String, Set<String>>();
                            teamfragmentDato.forEach(fragment -> {
                                var teamsGruppe = teams.getOrDefault(fragment.getEpost(), List.of("Tilhører ikke noe team"));
                                teamsGruppe.forEach(team -> {
                                    var teamSet = groupedTeam.getOrDefault(team, new HashSet<>());
                                    teamSet.add(fragment.getEpost());
                                    groupedTeam.put(team, teamSet);
                                });
                            });
                            return Mono.just(groupedTeam)
                                    .zipWith(Mono.just(teamfragmentDato.getFirst().getDato()));
                        }))
                .map(fragmentDato ->

                        DashboardTeamsDTO.builder()
                                .dato(fragmentDato.getT2())
                                .teams(fragmentDato.getT1().entrySet().stream()
                                        .map(fragment -> new DashboardTeamsDTO.Entry(
                                                fragment.getKey(),
                                                fragment.getValue().size()))
                                        .toList())
                                .build())
                .sort(Comparator.comparing(DashboardTeamsDTO::getDato).reversed());
    }
}
