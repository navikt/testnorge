package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private TeamBrukerRepository teamBrukerRepository;

    @Mock
    private BrukerRepository brukerRepository;

    @InjectMocks
    private TeamService teamService;

    private Bruker currentBruker;
    private Team team;

    @BeforeEach
    void setUp() {
        currentBruker = Bruker.builder()
                .id(1L)
                .brukerId("current_user")
                .brukernavn("Current User")
                .build();

        team = Team.builder()
                .id(10L)
                .navn("Test Team")
                .beskrivelse("Test Team Description")
                .brukere(new HashSet<>())
                .build();
        team.getBrukere().add(currentBruker);
    }

    @Test
    void fetchAllTeam_shouldReturnAllTeams() {
        when(teamRepository.findAll()).thenReturn(List.of(team));

        var result = teamService.fetchAllTeam();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isSameAs(team);
        verify(teamRepository).findAll();
    }

    @Test
    void fetchTeamById_shouldReturnTeam_whenTeamExists() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));

        var result = teamService.fetchTeamById(10L);

        assertThat(result).isSameAs(team);
        verify(teamRepository).findById(10L);
    }

    @Test
    void fetchTeamById_shouldThrowNotFoundException_whenTeamDoesNotExist() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.fetchTeamById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Fant ikke team med id=99");
    }

    @Test
    void opprettTeam_shouldCreateTeamWithCurrentUser() {
        var newTeam = Team.builder()
                .navn("New Team")
                .beskrivelse("New Team Description")
                .brukere(new HashSet<>())
                .build();

        var teamBruker = Bruker.builder()
                .id(2L)
                .brukernavn("New Team")
                .brukertype(Bruker.Brukertype.TEAM)
                .build();

        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentBruker);
        when(brukerService.createBruker(any(Bruker.class))).thenReturn(teamBruker);
        when(teamRepository.save(any(Team.class))).thenReturn(newTeam);

        var result = teamService.opprettTeam(newTeam);

        assertThat(result).isSameAs(newTeam);
        assertThat(newTeam.getBrukere()).contains(currentBruker);
        assertThat(newTeam.getBrukerId()).isEqualTo(2L);
        verify(teamRepository).save(newTeam);
    }

    @Test
    void updateTeam_shouldUpdateTeam_whenUserIsTeamMember() {
        var teamUpdates = Team.builder()
                .navn("Updated Team")
                .beskrivelse("Updated Description")
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentBruker);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        var result = teamService.updateTeam(10L, teamUpdates);

        assertThat(result).isSameAs(team);
        assertThat(team.getNavn()).isEqualTo("Updated Team");
        assertThat(team.getBeskrivelse()).isEqualTo("Updated Description");
        verify(teamRepository).save(team);
    }

    @Test
    void updateTeam_shouldThrowException_whenUserIsNotTeamMember() {
        var nonMember = Bruker.builder()
                .id(3L)
                .brukerId("non_member")
                .build();

        var teamWithoutCurrentUser = Team.builder()
                .id(10L)
                .brukere(Collections.emptySet())
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(teamWithoutCurrentUser));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(nonMember);

        assertThatThrownBy(() -> teamService.updateTeam(10L, new Team()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan ikke endre en gruppe man ikke selv er medlem i");

        verify(teamRepository, never()).save(any());
    }

    @Test
    void deleteTeamById_shouldDeleteTeamAndTeamMembers() {
        doNothing().when(teamRepository).deleteById(10L);
        doNothing().when(teamBrukerRepository).deleteAllById_TeamId(10L);

        teamService.deleteTeamById(10L);

        verify(teamRepository).deleteById(10L);
        verify(teamBrukerRepository).deleteAllById_TeamId(10L);
    }

    @Test
    void addBrukerToTeam_shouldAddUserToTeam_whenCurrentUserIsTeamMember() {
        var newMember = Bruker.builder()
                .id(4L)
                .brukerId("new_member")
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentBruker);
        when(brukerRepository.findBrukerByBrukerId("new_member")).thenReturn(Optional.of(newMember));
        when(teamBrukerRepository.existsById(any())).thenReturn(false);
        when(teamBrukerRepository.save(any())).thenReturn(new TeamBruker());

        teamService.addBrukerToTeam(10L, "new_member");

        verify(teamBrukerRepository).save(any(TeamBruker.class));
    }

    @Test
    void addBrukerToTeam_shouldThrowException_whenCurrentUserNotTeamMember() {
        var nonMember = Bruker.builder()
                .id(3L)
                .brukerId("non_member")
                .build();

        var newUser = Bruker.builder()
                .id(4L)
                .brukerId("new_user")
                .build();

        var teamWithoutCurrentUser = Team.builder()
                .id(10L)
                .brukere(Collections.emptySet())
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(teamWithoutCurrentUser));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(nonMember);
        when(brukerRepository.findBrukerByBrukerId("new_user")).thenReturn(Optional.of(newUser));

        assertThatThrownBy(() -> teamService.addBrukerToTeam(10L, "new_user"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan ikke legge til nytt gruppemedlem i en gruppe man ikke selv er medlem i");

        verify(teamBrukerRepository, never()).save(any());
    }

    @Test
    void removeBrukerFromTeam_shouldThrowException_whenUserIsNotTeamMember() {
        var nonMember = Bruker.builder()
                .id(3L)
                .brukerId("non_member")
                .build();

        var memberToRemove = Bruker.builder()
                .id(5L)
                .brukerId("member_to_remove")
                .build();

        var teamWithoutCurrentUser = Team.builder()
                .id(10L)
                .brukere(Collections.emptySet())
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(teamWithoutCurrentUser));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(nonMember);
        when(brukerRepository.findBrukerByBrukerId("member_to_remove")).thenReturn(Optional.of(memberToRemove));

        assertThatThrownBy(() -> teamService.removeBrukerFromTeam(10L, "member_to_remove"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan ikke fjerne et gruppemedlem fra en gruppe man ikke selv er medlem i");

        verify(teamBrukerRepository, never()).deleteById(any());
    }

    @Test
    void addBrukerToTeam_shouldThrowException_whenUserAlreadyTeamMember() {
        var existingMember = Bruker.builder()
                .id(5L)
                .brukerId("existing_member")
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentBruker);
        when(brukerRepository.findBrukerByBrukerId("existing_member")).thenReturn(Optional.of(existingMember));
        when(teamBrukerRepository.existsById(any())).thenReturn(true);

        assertThatThrownBy(() -> teamService.addBrukerToTeam(10L, "existing_member"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bruker er allerede medlem av dette teamet");

        verify(teamBrukerRepository, never()).save(any());
    }

    @Test
    void removeBrukerFromTeam_shouldRemoveUserFromTeam() {
        var memberToRemove = Bruker.builder()
                .id(5L)
                .brukerId("member_to_remove")
                .build();

        Set<Bruker> brukere = new HashSet<>();
        brukere.add(currentBruker);
        brukere.add(memberToRemove);

        team.setBrukere(brukere);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentBruker);
        when(brukerRepository.findBrukerByBrukerId("member_to_remove")).thenReturn(Optional.of(memberToRemove));
        doNothing().when(teamBrukerRepository).deleteById(any());

        teamService.removeBrukerFromTeam(10L, "member_to_remove");

        verify(teamBrukerRepository).deleteById(any());
    }

    @Test
    void removeBrukerFromTeam_shouldThrowException_whenRemovingLastMember() {
        var teamWithOnlyCurrentUser = Team.builder()
                .id(10L)
                .brukere(Set.of(currentBruker))
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(teamWithOnlyCurrentUser));
        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentBruker);
        when(brukerRepository.findBrukerByBrukerId("current_user")).thenReturn(Optional.of(currentBruker));

        assertThatThrownBy(() -> teamService.removeBrukerFromTeam(10L, "current_user"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Siste bruker i et team kan ikke fjerne seg selv");

        verify(teamBrukerRepository, never()).deleteById(any());
    }
}