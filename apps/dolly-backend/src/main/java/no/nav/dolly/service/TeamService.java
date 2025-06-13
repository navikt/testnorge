package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final BrukerService brukerService;
    private final TeamBrukerRepository teamBrukerRepository;
    private final BrukerRepository brukerRepository;

    public List<Team> fetchAllTeam() {
        return teamRepository.findAll();
    }

    public Team fetchTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fant ikke team med id=" + id));
    }

    @Transactional
    public Team opprettTeam(Team team) {
        var bruker = brukerService.fetchCurrentBrukerWithoutTeam();

        var brukerTeam = brukerService.createBruker(Bruker.builder()
                .brukernavn(team.getNavn())
                .brukertype(Bruker.Brukertype.TEAM)
                .build());

        brukerTeam.setBrukerId("team-bruker-id-" + brukerTeam.getId());

        team.getBrukere().add(bruker);
        team.setBrukerId(brukerTeam.getId());

        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(Long teamId, Team teamUpdates) {
        var existingTeam = fetchTeamById(teamId);

        var teamBruker = brukerRepository.findBrukerById(existingTeam.getBrukerId());

        var currentBruker = brukerService.fetchCurrentBrukerWithoutTeam();

        assertCurrentBrukerIsTeamMember(existingTeam);

        existingTeam.setNavn(teamUpdates.getNavn());
        teamBruker.ifPresent(bruker -> {
            bruker.setBrukernavn(teamUpdates.getNavn());
        });

        existingTeam.setBeskrivelse(teamUpdates.getBeskrivelse());

        if (nonNull(teamUpdates.getBrukere())) {
            existingTeam.getBrukere().clear();

            existingTeam.getBrukere().addAll(teamUpdates.getBrukere());

            if (existingTeam.getBrukere().stream()
                    .noneMatch(bruker -> bruker.getId()
                            .equals(currentBruker.getId()))) {
                existingTeam.getBrukere().add(currentBruker);
            }
        }

        return teamRepository.save(existingTeam);
    }

    @Transactional
    public void deleteTeamById(Long teamId) {

        var team = fetchTeamById(teamId);

        assertCurrentBrukerIsTeamMember(team);

        teamRepository.deleteById(teamId);
        teamBrukerRepository.deleteAllById_TeamId(teamId);
    }

    @Transactional
    public void addBrukerToTeam(Long teamId, String brukerId) {

        var nyttMedlem = brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Fant ikke bruker med id=" + brukerId));

        var teamBrukerId = TeamBruker.TeamBrukerId.builder()
                .teamId(teamId)
                .brukerId(nyttMedlem.getId())
                .build();

        if (teamBrukerRepository.existsById(teamBrukerId)) {
            throw new IllegalArgumentException("Bruker er allerede medlem av dette teamet");
        }
        teamBrukerRepository.save(TeamBruker.builder()
                .id(teamBrukerId)
                .build());
    }

    @Transactional
    public void removeBrukerFromTeam(Long teamId, String brukerId) {
        var team = fetchTeamById(teamId);

        var brukerToDelete = brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Fant ikke bruker med id=" + brukerId));

        assertCurrentBrukerIsTeamMember(team);

        if (team.getBrukere().size() == 1 &&
                team.getBrukere().stream().anyMatch(bruker -> bruker.getBrukerId().equals(brukerId))) {
            throw new IllegalArgumentException("Siste bruker i et team kan ikke fjerne seg selv, forsøk heller å slette teamet");
        }

        var id = TeamBruker.TeamBrukerId.builder()
                .teamId(teamId)
                .brukerId(brukerToDelete.getId())
                .build();

        teamBrukerRepository.deleteById(id);
    }

    private void assertCurrentBrukerIsTeamMember(Team team) {
        var currentBruker = brukerService.fetchCurrentBrukerWithoutTeam();

        var isCurrentUserTeamMember = nonNull(team.getBrukere()) &&
                team.getBrukere().stream().anyMatch(bruker -> bruker.getId().equals(currentBruker.getId()));

        if (!isCurrentUserTeamMember) {
            throw new IllegalArgumentException("Kan ikke utføre denne operasjonen på et team som brukeren ikke er medlem av");
        }
    }
}