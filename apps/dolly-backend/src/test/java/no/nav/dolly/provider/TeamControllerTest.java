package no.nav.dolly.provider;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private TeamController teamController;

    private Team team;
    private RsTeam rsTeam;
    private RsTeamWithBrukere rsTeamWithBrukere;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .id(1L)
                .navn("Test Team")
                .beskrivelse("Test Description")
                .brukere(new HashSet<>())
                .build();

        rsTeam = new RsTeam();
        rsTeam.setId(1L);
        rsTeam.setNavn("Test Team");
        rsTeam.setBeskrivelse("Test Description");
        rsTeam.setBrukere(Set.of("user1"));

        rsTeamWithBrukere = new RsTeamWithBrukere();
        rsTeamWithBrukere.setId(1L);
        rsTeamWithBrukere.setNavn("Test Team");
        rsTeamWithBrukere.setBeskrivelse("Test Description");
        rsTeamWithBrukere.setBrukere(Set.of());
    }

    @Test
    void getAllTeams_shouldReturnTeamsList() {
        when(teamService.fetchAllTeam()).thenReturn(List.of(team));
        when(mapperFacade.mapAsList(List.of(team), RsTeamWithBrukere.class)).thenReturn(List.of(rsTeamWithBrukere));

        List<RsTeamWithBrukere> response = teamController.getAllTeams();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst()).isEqualTo(rsTeamWithBrukere);
        verify(teamService).fetchAllTeam();
    }

    @Test
    void getTeamById_shouldReturnTeam() {
        when(teamService.fetchTeamById(1L)).thenReturn(team);
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        RsTeamWithBrukere response = teamController.getTeamById(1L);

        assertThat(response).isEqualTo(rsTeamWithBrukere);
        verify(teamService).fetchTeamById(1L);
    }

    @Test
    void createTeam_shouldReturnCreatedTeam() {
        when(mapperFacade.map(rsTeam, Team.class)).thenReturn(team);
        when(teamService.opprettTeam(team)).thenReturn(team);
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        RsTeamWithBrukere response = teamController.createTeam(rsTeam);

        assertThat(response).isEqualTo(rsTeamWithBrukere);
        verify(teamService).opprettTeam(team);
    }

    @Test
    void updateTeam_shouldReturnUpdatedTeam() {
        when(mapperFacade.map(rsTeam, Team.class)).thenReturn(team);
        when(teamService.updateTeam(eq(1L), any(Team.class))).thenReturn(team);
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        RsTeamWithBrukere response = teamController.updateTeam(1L, rsTeam);

        assertThat(response).isEqualTo(rsTeamWithBrukere);
        verify(teamService).updateTeam(eq(1L), any(Team.class));
    }

    @Test
    void deleteTeam_shouldCallServiceMethod() {
        doNothing().when(teamService).deleteTeamById(1L);

        teamController.deleteTeam(1L);

        verify(teamService, times(1)).deleteTeamById(1L);
    }

    @Test
    void addTeamMember_shouldCallServiceMethod() {
        doNothing().when(teamService).addBrukerToTeam(anyLong(), any(String.class));

        teamController.addTeamMember(1L, "user1");

        verify(teamService).addBrukerToTeam(1L, "user1");
    }

    @Test
    void removeTeamMember_shouldCallServiceMethod() {
        doNothing().when(teamService).removeBrukerFromTeam(anyLong(), any(String.class));

        teamController.removeTeamMember(1L, "user1");

        verify(teamService).removeBrukerFromTeam(1L, "user1");
    }
}