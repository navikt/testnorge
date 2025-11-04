package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUpdate;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final BrukerService brukerService;
    private final TeamBrukerRepository teamBrukerRepository;
    private final BrukerRepository brukerRepository;

    public Flux<Team> fetchAllTeam() {

        return fetchTeam(teamRepository::findAll);
    }

    public Mono<Team> fetchTeamById(Long id) {

        return fetchTeamByIdBasic(id)
                .flatMap(team -> fetchTeam(() -> Flux.from(teamRepository.findById(id)))
                        .next());
    }

    @Transactional
    public Mono<Team> opprettTeam(RsTeam team) {

        return assertTeamMember(team)
                .then(brukerRepository.save(Bruker.builder()
                                .brukernavn(team.getNavn())
                                .brukertype(Brukertype.TEAM)
                                .build())
                        .flatMap(brukerTeam -> {
                            brukerTeam.setBrukerId("team-bruker-id-%d".formatted(brukerTeam.getId()));
                            return brukerRepository.save(brukerTeam);
                        })
                        .flatMap(brukerTeam -> brukerService.fetchBrukerWithoutTeam()
                                .flatMap(bruker -> teamRepository.save(Team.builder()
                                                .navn(team.getNavn())
                                                .beskrivelse(team.getBeskrivelse())
                                                .opprettetAvId(bruker.getId())
                                                .opprettetTidspunkt(now())
                                                .brukerId(brukerTeam.getId())
                                                .build())
                                        .flatMap(lagretTeam -> teamBrukerRepository.save(TeamBruker.builder()
                                                        .brukerId(bruker.getId())
                                                        .teamId(lagretTeam.getId())
                                                        .opprettetTidspunkt(now())
                                                        .build())
                                                .map(ignore -> {
                                                    lagretTeam.setOpprettetAv(bruker);
                                                    lagretTeam.getBrukere().add(bruker);
                                                    return lagretTeam;
                                                })))));
    }

    @Transactional
    public Mono<Team> updateTeam(Long teamId, RsTeamUpdate teamUpdates) {

        return fetchTeamByIdBasic(teamId)
                .then(assertCurrentBrukerIsTeamMember(teamId))
                .then(teamRepository.findById(teamId)
                        .flatMap(existingTeam -> {
                            existingTeam.setNavn(teamUpdates.getNavn());
                            existingTeam.setBeskrivelse(teamUpdates.getBeskrivelse());
                            existingTeam.setOpprettetTidspunkt(now());
                            return teamRepository.save(existingTeam);
                        }))
                .flatMap(existingTeam -> brukerRepository.findById(existingTeam.getBrukerId())
                        .flatMap(bruker -> {
                            bruker.setBrukernavn(teamUpdates.getNavn());
                            return brukerRepository.save(bruker);
                        })
                        .flatMap(ignore -> {

                            if (!teamUpdates.getBrukere().isEmpty()) {

                                return teamBrukerRepository.deleteByTeamId(teamId)
                                        .then(brukerService.fetchOrCreateBruker()
                                                .flatMap(bruker -> {
                                                    teamUpdates.getBrukere().add(bruker.getBrukerId());
                                                    return Flux.fromIterable(teamUpdates.getBrukere())
                                                            .flatMap(brukerRepository::findByBrukerId)
                                                            .flatMap(tamBruker -> teamBrukerRepository.save(
                                                                    TeamBruker.builder()
                                                                            .teamId(teamId)
                                                                            .brukerId(tamBruker.getId())
                                                                            .opprettetTidspunkt(now())
                                                                            .build()))
                                                            .collectList();
                                                }));
                            }
                            return Mono.empty();
                        })
                        .then(fetchTeam(() -> Flux.from(teamRepository.findById(teamId)))
                                .next()));
    }

    @Transactional
    public Mono<Void> deleteTeamById(Long teamId) {

        return fetchTeamByIdBasic(teamId)
                .flatMap(team -> assertCurrentBrukerIsTeamMember(teamId)
                        .thenReturn(team))
                .flatMap(team -> {
                    var brukerId = team.getBrukerId();
                    return teamRepository.deleteById(teamId)
                            .then(teamBrukerRepository.deleteByTeamId(teamId))
                            .then(brukerRepository.deleteById(brukerId));
                });
    }

    @Transactional
    public Mono<Void> addBrukerToTeam(Long teamId, String brukerId) {

        return brukerRepository.findByBrukerId(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Fant ikke bruker med id %s".formatted(brukerId))))
                .flatMap(bruker -> teamBrukerRepository.existsByTeamIdAndBrukerId(teamId, bruker.getId())
                        .flatMap(teamBrukerExists ->
                                BooleanUtils.isTrue(teamBrukerExists) ?
                                        Mono.error(new IllegalArgumentException("Bruker er allerede medlem av dette teamet")) :
                                        teamBrukerRepository.save(
                                                TeamBruker.builder()
                                                        .teamId(teamId)
                                                        .brukerId(bruker.getId())
                                                        .opprettetTidspunkt(now())
                                                        .build())))
                .then();
    }

    @Transactional
    public Mono<Void> removeBrukerFromTeam(Long teamId, String brukerId) {

        return fetchTeamByIdBasic(teamId)
                .then(brukerRepository.findByBrukerId(brukerId))
                .flatMap(bruker -> teamBrukerRepository.findByTeamIdAndBrukerId(teamId, bruker.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Bruker er ikke medlem av dette teamet")))
                        .thenReturn(bruker))
                .flatMap(bruker -> teamBrukerRepository.findByTeamId(teamId)
                        .collectList()
                        .flatMap(teamBrukere -> {
                            if (teamBrukere.size() == 1 && teamBrukere.stream()
                                    .anyMatch(teamBruker -> teamBruker.getBrukerId().equals(bruker.getId()))) {
                                return Mono.error(new IllegalArgumentException(
                                        "Siste bruker i et team kan ikke fjerne seg selv, forsøk heller å slette teamet"));
                            } else {
                                return Mono.just(bruker);
                            }
                        }))
                .flatMap(teamBruker -> teamBrukerRepository.deleteByTeamIdAndBrukerId(teamId, teamBruker.getId()));
    }

    public Flux<Team> fetchTeam(Supplier<Flux<Team>> teamSupplier) {

        return brukerRepository.findAll()
                .reduce(new HashMap<Long, Bruker>(), (acc, bruker) -> {
                    acc.put(bruker.getId(), bruker);
                    return acc;
                })
                .flatMapMany(brukere -> teamBrukerRepository.findAll()
                        .collectList()
                        .flatMapMany(teamBrukere -> teamSupplier.get()
                                .flatMap(team -> {
                                    team.setOpprettetAv(brukere.get(team.getOpprettetAvId()));
                                    team.setBrukere(
                                            teamBrukere.stream()
                                                    .filter(teamBruker -> teamBruker.getTeamId().equals(team.getId()))
                                                    .map(teamBruker -> brukere.get(teamBruker.getBrukerId()))
                                                    .collect(Collectors.toSet()));
                                    return Mono.just(team);
                                })))
                .sort(Comparator.comparing(Team::getNavn));
    }

    private Mono<Void> assertCurrentBrukerIsTeamMember(Long teamId) {

        return brukerService.fetchBrukerWithoutTeam()
                .flatMap(bruker -> teamBrukerRepository.findByTeamIdAndBrukerId(teamId, bruker.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Kan ikke utføre operasjonen på team som bruker ikke er medlem av"))))
                .then();
    }

    private Mono<Void> assertTeamMember(RsTeam team) {

        if (isBlank(team.getNavn()) || isBlank(team.getBeskrivelse())) {
            return Mono.error(new IllegalArgumentException("Navn og beskrivelse må være satt for team"));
        }

        return teamRepository.findByNavn(team.getNavn())
                .switchIfEmpty(Mono.empty())
                .flatMap(ignore -> Mono.error(
                        new IllegalArgumentException("Team med navn '%s' finnes allerede".formatted(team.getNavn()))));
    }

    private Mono<Team> fetchTeamByIdBasic(Long id) {

        return teamRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Fant ikke team med id %d".formatted(id))));
    }
}