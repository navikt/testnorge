package no.nav.dolly.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceMedlemmerTest {
    private String medlem1 = "medlem1";
    private String medlem2 = "medlem2";
    private RsBruker medlem1bruker = RsBrukerBuilder.builder().navIdent(medlem1).build().convertToRealRsBruker();
    private RsBruker medlem2bruker = RsBrukerBuilder.builder().navIdent(medlem2).build().convertToRealRsBruker();
    private List<String> navidenter = Arrays.asList(medlem1, medlem2);

    @Mock
    TeamRepository teamRepository;

    @Mock
    BrukerRepository brukerRepository;

    @Mock
    MapperFacade mapperFacade;

    @InjectMocks
    TeamService teamService;

    @Before
    public void setupMocks() {
        //        when(brukerRepository.findBrukerByNavIdent("medlem1")).thenReturn(medlem1bruker);
        //        when(brukerRepository.findBrukerByNavIdent("medlem2")).thenReturn(null);
        //        when(brukerRepository.save(medlem2bruker)).thenReturn(medlem2bruker);
        //
        //        when(teamRepository.findTeamById(any())).thenReturn(team);
    }

    @Test
    public void addMedlemmer_LeggerTilMedlemmerITeam() {
        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>()).build().convertToRealTeam();

        RsBruker rb1 = RsBrukerBuilder.builder().navIdent("nav1").build().convertToRealRsBruker();
        RsBruker rb2 = RsBrukerBuilder.builder().navIdent("nav2").build().convertToRealRsBruker();
        List<RsBruker> inputBrukere = Arrays.asList(rb1, rb2);

        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent("nav1");
        b2.setNavIdent("nav2");

        when(teamRepository.findTeamById(any())).thenReturn(t);
        when(mapperFacade.mapAsList(inputBrukere, Bruker.class)).thenReturn(Arrays.asList(b1, b2));
        teamService.addMedlemmer(1l, inputBrukere);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(2));
    }

    @Test
    public void addMedlemmerByNavidenter_BrukereBasertPaaIdenterBlirLagtTilITeam() {
        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>()).build().convertToRealTeam();

        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent("nav1");
        b2.setNavIdent("nav2");

        when(teamRepository.findTeamById(any())).thenReturn(t);
        when(brukerRepository.findByNavIdentIn(navidenter)).thenReturn(Arrays.asList(b1, b2));

        teamService.addMedlemmerByNavidenter(1l, navidenter);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(2));
    }

    @Test
    public void fjernMedlemmer_fjernerMedlemHvisMedlemHarSammeNavidentSominput() {
        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent("nav1");
        b2.setNavIdent("nav2");

//        team.setMedlemmer(createBrukere());
//        assertTrue("Is saved Team empty?", !team.getMedlemmer().isEmpty());
//        teamService.fjernMedlemmer(team.getId(), navidenter);
//        Team savedTeam = captureTheTeamSavedToRepo();
//        assertTrue("Is saved Team empty?", savedTeam.getMedlemmer() == null || savedTeam.getMedlemmer().isEmpty());
    }

    private Team captureTheTeamSavedToRepo() {
        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

}