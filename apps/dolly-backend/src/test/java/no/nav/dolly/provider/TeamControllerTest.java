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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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

        when(teamService.fetchAllTeam()).thenReturn(Flux.just(team));
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        StepVerifier.create(teamController.getAllTeams()
                        .collectList())
                .assertNext(response -> {
                                assertThat(response).hasSize(1);
                                assertThat(response.getFirst()).isEqualTo(rsTeamWithBrukere);
                        verify(teamService).fetchAllTeam();
                        })
                .verifyComplete();
    }

    @Test
    void getTeamById_shouldReturnTeam() {

        when(teamService.fetchTeamById(1L)).thenReturn(Mono.just(team));
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        StepVerifier.create(teamController.getTeamById(1L))
                .assertNext(response -> {
                    assertThat(response).isEqualTo(rsTeamWithBrukere);
                verify(teamService).fetchTeamById(1L);
                })
                .verifyComplete();
    }

    @Test
    void createTeam_shouldReturnCreatedTeam() {

        when(teamService.opprettTeam(rsTeam)).thenReturn(Mono.just(team));
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        StepVerifier.create(teamController.createTeam(rsTeam))
                .assertNext(response -> {
                    assertThat(response).isEqualTo(rsTeamWithBrukere);
                    verify(teamService).opprettTeam(rsTeam);
                })
                .verifyComplete();
    }

    @Test
    void updateTeam_shouldReturnUpdatedTeam() {

        when(teamService.updateTeam(eq(1L), any(RsTeam.class))).thenReturn(Mono.just(team));
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeamWithBrukere);

        StepVerifier.create(teamController.updateTeam(1L, rsTeam))
                .assertNext(response -> {
                    assertThat(response).isEqualTo(rsTeamWithBrukere);
                    verify(teamService).updateTeam(eq(1L), any(RsTeam.class));
                })
                .verifyComplete();
    }

    @Test
    void deleteTeam_shouldCallServiceMethod() {

        when(teamService.deleteTeamById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(teamController.deleteTeam(1L))
                .expectNextCount(0)
                .verifyComplete();

        verify(teamService).deleteTeamById(1L);
    }

    @Test
    void addTeamMember_shouldCallServiceMethod() {

        when(teamService.addBrukerToTeam(anyLong(), any(String.class))).thenReturn(Mono.empty());

        StepVerifier.create(teamController.addTeamMember(1L, "user1"))
                .expectNextCount(0)
                .verifyComplete();

        verify(teamService).addBrukerToTeam(1L, "user1");
    }

    @Test
    void removeTeamMember_shouldCallServiceMethod() {

        when(teamService.removeBrukerFromTeam(anyLong(), any(String.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(teamController.removeTeamMember(1L, "user1"))
                .expectNextCount(0)
                .verifyComplete();

        verify(teamService).removeBrukerFromTeam(1L, "user1");
    }
}