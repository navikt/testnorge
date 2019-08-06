package no.nav.dolly.provider.api;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.TeamService;
import no.nav.dolly.service.TestgruppeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTest {

    private static final Long TEAM_ID = 11L;
    private static final String NAV_IDENT = "Z999999";

    @Mock
    private TeamService teamService;

    @Mock
    private TestgruppeService testgruppeService;

    @InjectMocks
    private TeamController controller;

    @Test
    public void getTeams_hvisIdentTilstedetSaaHentesBasertPaaIdent() {
        String navident = "nav";
        controller.getTeams(navident);
        verify(teamService).fetchTeamsByMedlemskapInTeamsMapped(navident);
    }

    @Test
    public void getTeams_hvisIdentErFravaerendeSaaHentAlleTeams() {
        controller.getTeams(null);
        verify(teamService).findAllOrderByNavn();
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
        verify(teamService).getTeamById(TEAM_ID);
    }

    @Test
    public void addBrukereSomTeamMedlemmerByNavidenter() {
        List<String> navidenter = singletonList("test");

        controller.addBrukereSomTeamMedlemmerByNavidenter(TEAM_ID, navidenter);
        verify(teamService).addMedlemmerByNavidenter(TEAM_ID, navidenter);
    }

    @Test
    public void fjernBrukerefraTeam() {
        List<String> navidenter = singletonList("test");

        controller.fjernBrukerefraTeam(TEAM_ID, navidenter);
        verify(teamService).fjernMedlemmer(TEAM_ID, navidenter);
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