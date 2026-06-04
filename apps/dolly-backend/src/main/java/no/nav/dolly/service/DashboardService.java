package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.altinn3.Altinn3TilgangServiceConsumer;
import no.nav.dolly.consumer.altinn3.dto.Altinn3TilgangDTO;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.dolly.consumer.teamkatalog.TeamkatalogConsumer;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.domain.dto.DashboardDollyTeamsDTO;
import no.nav.dolly.domain.dto.DashboardOrganisasjonerDTO;
import no.nav.dolly.domain.dto.DashboardPersonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.DollyTeam2Fragment;
import no.nav.dolly.domain.projection.DollyTeamFragment;
import no.nav.dolly.domain.projection.OrganisasjonFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
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

import static java.lang.Math.toIntExact;
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
    private final TeamRepository teamRepository;

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
                .flatMap(teamkatalogConsumer::getTeamForEpost, 3)
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

    public Flux<DashboardOrganisasjonerDTO> getOrganisasjonerStatus() {

        return Mono.zip(altinn3TilgangServiceConsumer.getOrganisasjoner()
                                .collect(Collectors.toMap(Altinn3TilgangDTO::getOrganisasjonsnummer, value -> value)),
                        brukerServiceConsumer.getAlleBrukere()
                                .collect(Collectors.toMap(BrukerDTO::getId, BrukerDTO::getOrganisasjonsnummer)))
                .flatMapMany(oppslag -> bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert()
                        .groupBy(OrganisasjonFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .flatMap(fragments -> Mono.just(
                                Tuples.of(groupFragmentsByOrganisasjon(fragments, oppslag.getT2()), fragments.getFirst().getInterval())))
                        .map(tuple -> DashboardOrganisasjonerDTO.builder()
                                .interval(tuple.getT2())
                                .organisasjoner(tuple.getT1().entrySet().stream()
                                        .map(entry -> toOrganisasjonEntry(entry.getKey(), entry.getValue(), oppslag.getT1()))
                                        .sorted(Comparator.comparing(DashboardOrganisasjonerDTO.Entry::getNavn))
                                        .toList())
                                .totaltAntallOrganisasjoner(tuple.getT1().size())
                                .totaltUnikeBrukere((int) tuple.getT1().values().stream()
                                        .flatMap(Set::stream)
                                        .distinct()
                                        .count())
                                .build()))
                .sort(Comparator.comparing(DashboardOrganisasjonerDTO::getInterval).reversed());
    }

    public Flux<DashboardDollyTeamsDTO> getDollyTeamsStatus() {

        return teamRepository.findAllTeamBrukere()
                .collect(Collectors.toMap(DollyTeam2Fragment::getBrukerid, DollyTeam2Fragment::getAntall))
                .flatMapMany(oppslag -> bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert()
                        .groupBy(DollyTeamFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .map(fragments -> {
                            var teams = fragments.stream()
                                    .map(fragment -> toDollyTeamEntry(fragment, oppslag))
                                    .sorted(Comparator.comparing(DashboardDollyTeamsDTO.Entry::getNavn))
                                    .toList();
                            return DashboardDollyTeamsDTO.builder()
                                    .interval(fragments.getFirst().getInterval())
                                    .teams(teams)
                                    .totaltAntallTeams(teams.size())
                                    .totaltAntallMedlemmer(teams.stream()
                                            .mapToInt(DashboardDollyTeamsDTO.Entry::getAntallMedlemmer)
                                            .sum())
                                    .build();
                        }))
                .sort(Comparator.comparing(DashboardDollyTeamsDTO::getInterval).reversed());
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

    private static Map<String, Set<String>> groupFragmentsByOrganisasjon(List<OrganisasjonFragment> fragments,
                                                                         Map<String, String> brukerToOrgnummer) {

        var grouped = new HashMap<String, Set<String>>();
        fragments.forEach(fragment -> {
            var orgNummer = brukerToOrgnummer.get(fragment.getBrukerid());
            grouped.computeIfAbsent(orgNummer, _ -> new HashSet<>()).add(fragment.getBrukerid());
        });
        return grouped;
    }

    private static DashboardOrganisasjonerDTO.Entry toOrganisasjonEntry(String orgNummer,
                                                                        Set<String> brukere,
                                                                        Map<String, Altinn3TilgangDTO> organisasjonerOppslag) {

        var info = organisasjonerOppslag.getOrDefault(orgNummer, Altinn3TilgangDTO.builder()
                .navn("Ukjent organisasjon")
                .organisasjonsform("Ukjent organisasjonsform")
                .build());
        return new DashboardOrganisasjonerDTO.Entry(orgNummer, info.getNavn(), info.getOrganisasjonsform(), brukere.size());
    }

    private static DashboardDollyTeamsDTO.Entry toDollyTeamEntry(DollyTeamFragment fragment,
                                                                  Map<String, Long> oppslag) {

        var info = fragment.getInformasjon().split("\\|");
        return new DashboardDollyTeamsDTO.Entry(info[0], info[1], toIntExact(oppslag.get(info[2])));
    }
}
