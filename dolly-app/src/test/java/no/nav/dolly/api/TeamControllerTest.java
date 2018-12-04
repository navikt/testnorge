package no.nav.dolly.api;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
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
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.TeamService;

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
        controller.getTeams(Optional.of(navident));
        verify(teamService).fetchTeamsByMedlemskapInTeams(navident);
    }

    @Test
    public void getTeams_hvisIdentErFravaerendeSaaHentAlleTeams() {
        controller.getTeams(Optional.empty());
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
        RsTeamUtvidet team = new RsTeamUtvidet();

        controller.endreTeaminfo(1l, team);
        verify(teamService).updateTeamInfo(id, team);
    }
}