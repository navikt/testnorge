package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.TeamService;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TeamControllerTest {

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
        controller.getTeams(navident);
        verify(teamService).fetchTeamsByMedlemskapInTeams(navident);
    }

    @Test
    public void getTeams_hvisIdentErFravaerendeSaaHentAlleTeams() {
        controller.getTeams(null);
        verify(teamRepository).findAll();
    }

    @Test
    public void opprettTeam() {
        RsOpprettTeam res = new RsOpprettTeam();
        controller.opprettTeam(res);
        verify(teamService).opprettTeam(res);
    }

    @Test
    public void deleteTeam() {
        Long id = 1l;
        controller.deleteTeam(id);
        verify(teamService).deleteTeam(id);
    }

    @Test
    public void fetchTeamById() {
        Long id = 1l;
        controller.fetchTeamById(id);
        verify(teamService).fetchTeamById(id);
    }

    @Test
    public void addBrukereSomTeamMedlemmerByNavidenter() {
        Long id = 1l;
        List<String> navidenter = Arrays.asList("test");

        controller.addBrukereSomTeamMedlemmerByNavidenter(id, navidenter);
        verify(teamService).addMedlemmerByNavidenter(id, navidenter);
    }

    @Test
    public void fjernBrukerefraTeam() {
        Long id = 1l;
        List<String> navidenter = Arrays.asList("test");

        controller.fjernBrukerefraTeam(id, navidenter);
        verify(teamService).fjernMedlemmer(id, navidenter);
    }

    @Test
    public void endreTeaminfo() {
        Long id = 1l;
        RsTeam t = new RsTeam();

        controller.endreTeaminfo(1l, t);
        verify(teamService).updateTeamInfo(id, t);
    }
}