package no.nav.dolly.api;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.TeamService;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTest {

    private static final Long TEAM_ID = 11L;
    private static final String NAV_IDENT = "Z999999";

    @Mock
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private TeamController controller;

    @Test
    public void getTeams_hvisIdentTilstedetSaaHentesBasertPaaIdent() {
        String navident = "nav";
        controller.getTeams(Optional.of(navident));
        verify(teamService).fetchTeamsByMedlemskapInTeams(navident);
    }

    @Test
    public void getTeams_hvisIdentErFravaerendeSaaHentAlleTeams() {
        controller.getTeams(Optional.empty());
        verify(teamRepository).findAllByOrderByNavn();
    }

    @Test
    public void opprettTeam() {
        RsOpprettTeam res = new RsOpprettTeam();
        controller.opprettTeam(res);
        verify(teamService).opprettTeam(res);
    }

    @Test(expected = NotFoundException.class)
    public void deleteTeamNotFound() {
        controller.deleteTeam(TEAM_ID);
        verify(teamService).deleteTeam(TEAM_ID);
    }

    @Test
    public void deleteTeam() {
        when(teamService.deleteTeam(TEAM_ID)).thenReturn(1);
        controller.deleteTeam(TEAM_ID);
        verify(teamService).deleteTeam(TEAM_ID);
    }

    @Test
    public void fetchTeamById() {

        controller.fetchTeamById(TEAM_ID);
        verify(teamService).fetchTeamById(TEAM_ID);
    }

    @Test
    public void addBrukereSomTeamMedlemmerByNavidenter() {
        List<String> navidenter = singletonList("test");

        controller.addBrukereSomTeamMedlemmerByNavidenter(TEAM_ID, navidenter);
        verify(teamService).addMedlemmerByNavidenter(TEAM_ID, navidenter);
    }

    @Test(expected = NotFoundException.class)
    public void slettMedlemFraTeamNotFound() {
        when(teamService.slettMedlem(TEAM_ID, NAV_IDENT)).thenThrow(NotFoundException.class);
        controller.deleteMedlemfraTeam(TEAM_ID, NAV_IDENT);
    }

    @Test
    public void slettMedlemFraTeamOK() {
        when(teamService.slettMedlem(TEAM_ID, NAV_IDENT)).thenReturn(new RsTeamUtvidet());
        controller.deleteMedlemfraTeam(TEAM_ID, NAV_IDENT);
        verify(teamService).slettMedlem(TEAM_ID, NAV_IDENT);
    }

    @Test
    public void endreTeaminfo() {
        RsTeamUtvidet team = new RsTeamUtvidet();

        controller.endreTeaminfo(TEAM_ID, team);
        verify(teamService).updateTeamInfo(TEAM_ID, team);
    }
}