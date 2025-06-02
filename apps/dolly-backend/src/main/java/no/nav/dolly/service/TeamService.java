package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final BrukerService brukerService;
    private final TeamBrukerRepository teamBrukerRepository;

    public List<Team> fetchAllTeam() {
        return teamRepository.findAll();
    }

    public Team fetchTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fant ikke team med id=" + id));
    }

    @Transactional
    public Team opprettTeam(Team team) {
        Bruker bruker = brukerService.fetchOrCreateBruker();

        team.setOpprettetAv(bruker);
        Team savedTeam = teamRepository.save(team);

        addBrukerToTeam(savedTeam.getId(), bruker.getId());

        return savedTeam;
    }

    @Transactional
    public Team updateTeam(Long teamId, Team teamUpdates) {
        Team team = fetchTeamById(teamId);

        Bruker currentBruker = brukerService.fetchOrCreateBruker();

        boolean isCurrentUserTeamMember = team.getBrukere() != null &&
                team.getBrukere().stream().anyMatch(bruker -> bruker.getId().equals(currentBruker.getId()));

        if (!isCurrentUserTeamMember) {
            throw new IllegalArgumentException("Kan ikke endre en gruppe man ikke selv er medlem i");
        }

        team.setNavn(teamUpdates.getNavn());
        team.setBeskrivelse(teamUpdates.getBeskrivelse());

        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeamById(Long teamId) {
        teamRepository.deleteById(teamId);
    }

    @Transactional
    public void addBrukerToTeam(Long teamId, Long brukerId) {
        Team team = fetchTeamById(teamId);

        Bruker currentBruker = brukerService.fetchOrCreateBruker();

        boolean isCurrentUserTeamMember = team.getBrukere() != null &&
                team.getBrukere().stream().anyMatch(bruker -> bruker.getId().equals(currentBruker.getId()));

        if (!isCurrentUserTeamMember) {
            throw new IllegalArgumentException("Kan ikke legge til nytt gruppemedlem i en gruppe man ikke selv er medlem i");
        }

        TeamBruker.TeamBrukerId id = TeamBruker.TeamBrukerId.builder()
                .teamId(teamId)
                .brukerId(brukerId)
                .build();

        if (!teamBrukerRepository.existsById(id)) {
            TeamBruker teamBruker = TeamBruker.builder()
                    .id(id)
                    .build();
            teamBrukerRepository.save(teamBruker);
        }
    }

    @Transactional
    public void removeBrukerFromTeam(Long teamId, Long brukerId) {
        Team team = fetchTeamById(teamId);

        Bruker currentBruker = brukerService.fetchOrCreateBruker();

        boolean isCurrentUserTeamMember = team.getBrukere() != null &&
                team.getBrukere().stream().anyMatch(bruker -> bruker.getId().equals(currentBruker.getId()));

        if (!isCurrentUserTeamMember) {
            throw new IllegalArgumentException("Kan ikke fjerne et gruppemedlem fra en gruppe man ikke selv er medlem i");
        }

        if (team.getBrukere().size() == 1 && team.getBrukere().stream().anyMatch(bruker -> bruker.getId().equals(brukerId))) {
            throw new IllegalArgumentException("Siste bruker i et team kan ikke fjerne seg selv, forsøk heller å slette teamet");
        }

        TeamBruker.TeamBrukerId id = TeamBruker.TeamBrukerId.builder()
                .teamId(teamId)
                .brukerId(brukerId)
                .build();

        teamBrukerRepository.deleteById(id);
    }
}