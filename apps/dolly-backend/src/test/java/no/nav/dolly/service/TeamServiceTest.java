package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUpdate;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    private TeamBruker teamBruker;
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
                .brukere(Set.of(currentBruker))
                .brukerId(22L)
                .build();

        teamBruker = TeamBruker.builder()
                .id(2L)
                .brukerId(team.getId())
                .teamId(team.getId())
                .build();
    }

    @Test
    void fetchAllTeam_shouldReturnAllTeams() {

        when(teamRepository.findAll()).thenReturn(Flux.just(team));
        when(brukerRepository.findAll()).thenReturn(Flux.just(currentBruker));
        when(teamBrukerRepository.findAll()).thenReturn(Flux.just(teamBruker));

        StepVerifier.create(teamService.fetchAllTeam()
                        .collectList())
                .assertNext(result -> {
                    assertThat(result).isNotEmpty();
                    assertThat(result.getFirst()).isSameAs(team);
                    verify(teamRepository).findAll();
                })
                .verifyComplete();
    }

    @Test
    void fetchTeamById_shouldReturnTeam_whenTeamExists() {

        when(teamRepository.findById(10L)).thenReturn(Mono.just(team));
        when(brukerRepository.findAll()).thenReturn(Flux.just(currentBruker));
        when(teamBrukerRepository.findAll()).thenReturn(Flux.just(teamBruker));

        StepVerifier.create(teamService.fetchTeamById(10L))
                .assertNext(result -> {
                    assertThat(result).isSameAs(team);
                    verify(teamRepository, times(2)).findById(10L);
                })
                .verifyComplete();
    }

    @Test
    void fetchTeamById_shouldThrowNotFoundException_whenTeamDoesNotExist() {

        when(teamRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(teamService.fetchTeamById(99L))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().contains("Fant ikke team med id 99"))
                .verify();
    }

    @Test
    void opprettTeam_shouldCreateTeamWithCurrentUser() {

        var rsTeam = RsTeam.builder()
                .navn("New Team")
                .beskrivelse("New Team Description")
                .build();

        var newTeam = Team.builder()
                .navn(rsTeam.getNavn())
                .beskrivelse(rsTeam.getBeskrivelse())
                .brukerId(2L)
                .brukere(new HashSet<>())
                .build();

        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(currentBruker));

        var teamBruker1 = Bruker.builder()
                .id(2L)
                .brukernavn("New Team")
                .brukertype(Bruker.Brukertype.TEAM)
                .build();

        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(currentBruker));
        when(teamRepository.save(any(Team.class))).thenReturn(Mono.just(newTeam));
        when(brukerRepository.save(any(Bruker.class))).thenReturn(Mono.just(teamBruker1));
        when(teamBrukerRepository.save(any(TeamBruker.class))).thenReturn(Mono.just(new TeamBruker()));
        when(teamRepository.findByNavn(anyString())).thenReturn(Mono.empty());
        when(brukerRepository.findByBrukerIdIn(any())).thenReturn(Flux.just(teamBruker1));

        StepVerifier.create(teamService.opprettTeam(rsTeam))
                .assertNext(result -> {
                    assertThat(result).isSameAs(newTeam);
                    assertThat(newTeam.getBrukere()).contains(currentBruker);
                    assertThat(newTeam.getBrukerId()).isEqualTo(2L);
                    verify(teamRepository).save(any(Team.class));
                })
                .verifyComplete();
    }

    @Test
    void updateTeam_shouldUpdateTeam_whenUserIsTeamMember() {

        var teamUpdates = RsTeamUpdate.builder()
                .navn("Updated Team")
                .beskrivelse("Updated Description")
                .build();

        var teamBruker1 = Bruker.builder()
                .id(2L)
                .brukernavn("Test Team")
                .build();

        when(teamRepository.findById(10L)).thenReturn(Mono.just(team));
        when(brukerRepository.findById(team.getBrukerId())).thenReturn(Mono.just(teamBruker1));
        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(currentBruker));
        when(teamRepository.save(any(Team.class))).thenReturn(Mono.just(team));
        when(brukerRepository.findAll()).thenReturn(Flux.just(currentBruker));
        when(teamBrukerRepository.findAll()).thenReturn(Flux.just(teamBruker));
        when(teamBrukerRepository.findByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.just(teamBruker));
        when(brukerRepository.save(any(Bruker.class))).thenReturn(Mono.just(teamBruker1));

        StepVerifier.create(teamService.updateTeam(10L, teamUpdates))
                .assertNext(result -> {
                    assertThat(result).isSameAs(team);
                    assertThat(team.getNavn()).isEqualTo("Updated Team");
                    assertThat(team.getBeskrivelse()).isEqualTo("Updated Description");
                    verify(teamRepository).save(team);
                })
                .verifyComplete();
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

        when(teamRepository.findById(10L)).thenReturn(Mono.just(teamWithoutCurrentUser));
        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(nonMember));
        when(teamBrukerRepository.findByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(teamService.updateTeam(10L, new RsTeamUpdate()))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Kan ikke utføre operasjonen på team som bruker ikke er medlem av"))
                .verify();

        verify(teamRepository, never()).save(any());
    }

    @Test
    void deleteTeamById_shouldDeleteTeamAndTeamMembers() {

        when(teamRepository.findById(10L)).thenReturn(Mono.just(team));
        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(currentBruker));
        when(teamRepository.deleteById(10L)).thenReturn(Mono.empty());
        when(teamBrukerRepository.deleteByTeamId(10L)).thenReturn(Mono.empty());
        when(teamBrukerRepository.findByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.just(teamBruker));
        when(brukerRepository.deleteById(22L)).thenReturn(Mono.empty());

        StepVerifier.create(teamService.deleteTeamById(10L))
                .expectComplete()
                .verify();

        verify(teamRepository).deleteById(10L);
        verify(teamBrukerRepository).deleteByTeamId(10L);
        verify(brukerRepository).deleteById(22L);
    }

    @Test
    void addBrukerToTeam_shouldAddUserToTeam_whenUserNotAlreadyMember() {

        var newMember = Bruker.builder()
                .id(4L)
                .brukerId("new_member")
                .build();

        when(brukerRepository.findByBrukerId("new_member")).thenReturn(Mono.just(newMember));
        when(teamBrukerRepository.save(any())).thenReturn(Mono.just(new TeamBruker()));
        when(teamBrukerRepository.existsByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(teamService.addBrukerToTeam(10L, "new_member"))
                .expectComplete()
                .verify();

        verify(teamBrukerRepository).save(any(TeamBruker.class));
    }

    @Test
    void addBrukerToTeam_shouldThrowException_whenUserAlreadyTeamMember() {

        var existingMember = Bruker.builder()
                .id(5L)
                .brukerId("existing_member")
                .build();

        when(brukerRepository.findByBrukerId("existing_member")).thenReturn(Mono.just(existingMember));
        when(teamBrukerRepository.existsByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(teamService.addBrukerToTeam(10L, "existing_member"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Bruker er allerede medlem av dette teamet"))
                .verify();

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

        when(teamRepository.findById(10L)).thenReturn(Mono.just(team));
        when(brukerRepository.findByBrukerId("member_to_remove")).thenReturn(Mono.just(memberToRemove));
        when(teamBrukerRepository.deleteByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.empty());
        when(teamBrukerRepository.findByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.just(teamBruker));
        when(teamBrukerRepository.findByTeamId(10L)).thenReturn(Flux.just(teamBruker, teamBruker));

        StepVerifier.create(teamService.removeBrukerFromTeam(10L, "member_to_remove"))
                .expectComplete()
                .verify();

        verify(teamBrukerRepository).deleteByTeamIdAndBrukerId(anyLong(), anyLong());
    }

    @Test
    void removeBrukerFromTeam_shouldThrowException_whenRemovingLastMember() {

        var teamWithOnlyCurrentUser = Team.builder()
                .id(10L)
                .brukere(Set.of(currentBruker))
                .brukerId(1L)
                .build();

        var teamBruker2 = TeamBruker.builder()
                .id(1L)
                .teamId(10L)
                .brukerId(currentBruker.getId())
                .build();

        when(teamRepository.findById(10L)).thenReturn(Mono.just(teamWithOnlyCurrentUser));
        when(brukerRepository.findByBrukerId("current_user")).thenReturn(Mono.just(currentBruker));
        when(teamBrukerRepository.findByTeamId(anyLong())).thenReturn(Flux.just(teamBruker2));
        when(teamBrukerRepository.findByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.just(teamBruker2));

        StepVerifier.create(teamService.removeBrukerFromTeam(10L, "current_user"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Siste bruker i et team kan ikke fjerne seg selv, forsøk heller å slette teamet"))
                .verify();

        verify(teamBrukerRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void removeBrukerFromTeam_shouldThrowException_whenUserIsNotTeamMember() {

        var memberToRemove = Bruker.builder()
                .id(5L)
                .brukerId("member_to_remove")
                .build();

        when(teamRepository.findById(10L)).thenReturn(Mono.just(team));
        when(brukerRepository.findByBrukerId("member_to_remove")).thenReturn(Mono.just(memberToRemove));
        when(teamBrukerRepository.findByTeamIdAndBrukerId(anyLong(), anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(teamService.removeBrukerFromTeam(10L, "member_to_remove"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Bruker er ikke medlem av dette teamet"))
                .verify();

        verify(teamBrukerRepository, never()).deleteById(any(Long.class));
    }
}