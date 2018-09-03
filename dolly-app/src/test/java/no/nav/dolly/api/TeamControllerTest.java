package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultSet.RsTeam;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.TeamService;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;
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
    public void getTeams() {

    }

    @Test
    public void opprettTeam() {
    }

    @Test
    public void deleteTeam() {
    }

    @Test
    public void fetchTeamById() {
    }

    @Test
    public void addBrukereSomTeamMedlemmerByNavidenter() {
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