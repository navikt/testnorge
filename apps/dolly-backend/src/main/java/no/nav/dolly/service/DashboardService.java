package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.altinn3.Altinn3TilgangServiceConsumer;
import no.nav.dolly.consumer.altinn3.dto.Altinn3TilgangDTO;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.dolly.consumer.teamkatalog.TeamkatalogConsumer;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.domain.dto.DashboardOrganisasjonerDTO;
import no.nav.dolly.domain.dto.DashboardPersonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.OrganisasjonFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final String INGEN_TEAM = "Tilhører ikke noe team";

    private final Altinn3TilgangServiceConsumer altinn3TilgangServiceConsumer;
    private final BestillingRepository bestillingRepository;
    private final BrukerRepository brukerRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final TeamkatalogConsumer teamkatalogConsumer;

    public Flux<DashboardPersonerDTO> getPersonerStatus() {

        return bestillingRepository.findBestillingerOrderBySistOppdatert()
                .groupBy(BestillingerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(fragmentliste ->
                        DashboardPersonerDTO.builder()
                                .dato(fragmentliste.getFirst().getDato())
                                .personerTotalt(fragmentliste.stream()
                                        .mapToLong(BestillingerFragment::getPersoner).sum())
                                .nye(sumByStatus(fragmentliste, BestillingerFragment::getGjenopprettstatus, "NYBESTILLING"))
                                .gjenopprettede(sumByStatus(fragmentliste, BestillingerFragment::getGjenopprettstatus, "GJENOPPRETTING"))
                                .pdlFeil(sumByStatus(fragmentliste, BestillingerFragment::getPdlstatus, "FEIL"))
                                .andreFeil(sumByStatus(fragmentliste, BestillingerFragment::getAnnenstatus, "FEIL"))
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
                        .groupBy(TeamFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .flatMap(fragments -> Mono.just(
                                Tuples.of(groupFragmentsByTeam(fragments, teams), fragments.getFirst().getInterval()))))
                .map(tuple ->
                        DashboardTeamsDTO.builder()
                                .interval(tuple.getT2())
                                .teams(tuple.getT1().entrySet().stream()
                                        .map(entry -> new DashboardTeamsDTO.Entry(entry.getKey(), entry.getValue().size()))
                                        .sorted(Comparator.comparing(DashboardTeamsDTO.Entry::getTeam))
                                        .toList())
                                .totaltAntallTeams(tuple.getT1().size())
                                .totaltUnikeBrukere((int) tuple.getT1().values().stream()
                                        .flatMap(Set::stream)
                                        .distinct()
                                        .count())
                                .build())
                .sort(Comparator.comparing(DashboardTeamsDTO::getInterval).reversed());
    }

    private static long sumByStatus(List<BestillingerFragment> fragments,
                                    Function<BestillingerFragment, String> statusGetter,
                                    String value) {

        return fragments.stream()
                .filter(f -> value.equals(statusGetter.apply(f)))
                .mapToLong(BestillingerFragment::getPersoner)
                .sum();
    }

    private static Map<String, Set<String>> groupFragmentsByTeam(List<TeamFragment> fragments,
                                                                 Map<String, List<String>> teams) {

        var grouped = new HashMap<String, Set<String>>();
        fragments.forEach(fragment -> {
            var teamNames = teams.getOrDefault(fragment.getEpost(), List.of(INGEN_TEAM));
            teamNames.forEach(team ->
                    grouped.computeIfAbsent(team, _ -> new HashSet<>()).add(fragment.getEpost()));
        });
        return grouped;
    }

    public Flux<DashboardOrganisasjonerDTO> getOrganisasjonerStatus() {

        return Mono.zip(altinn3TilgangServiceConsumer.getOrganisasjoner()
                                .collect(Collectors.toMap(Altinn3TilgangDTO::getOrganisasjonsnummer, Altinn3TilgangDTO::getNavn)),
                        brukerServiceConsumer.getAlleBrukere()
                                .collect(Collectors.toMap(BrukerDTO::getId, BrukerDTO::getOrganisasjonsnummer)))
                .flatMapMany(oppslag -> bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert()
                        .groupBy(OrganisasjonFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .flatMap(fragments -> {
                            var organisasjoner = new HashMap<String, Set<String>>();
                            fragments.forEach(fragment -> {
                                var orgNummer = oppslag.getT2().get(fragment.getBrukerid());
                                organisasjoner.computeIfAbsent(orgNummer, _ -> new HashSet<>())
                                        .add(fragment.getBrukerid());
                            });
                            return Mono.just(organisasjoner)
                                    .zipWith(Mono.just(fragments.getFirst().getInterval()));
                        })
                        .map(organisasjoner -> DashboardOrganisasjonerDTO.builder()
                                .interval(organisasjoner.getT2())
                                .organisasjoner(organisasjoner.getT1().entrySet().stream()
                                        .map(entry -> new DashboardOrganisasjonerDTO.Entry(
                                                entry.getKey(),
                                                oppslag.getT1().getOrDefault(entry.getKey(), "Ukjent organisasjon"),
                                                entry.getValue().size()))
                                        .sorted(Comparator.comparing(DashboardOrganisasjonerDTO.Entry::getNavn))
                                        .toList())
                                .totaltAntallOrganisasjoner(organisasjoner.getT1().size())
                                .totaltUnikeBrukere((int) organisasjoner.getT1().values().stream()
                                        .flatMap(Set::stream)
                                        .distinct()
                                        .count())
                                .build()))
                .sort(Comparator.comparing(DashboardOrganisasjonerDTO::getInterval).reversed());
    }
}
