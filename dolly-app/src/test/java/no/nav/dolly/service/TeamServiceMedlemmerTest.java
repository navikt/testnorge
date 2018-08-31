package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.domain.resultSet.RsOpprettTeam;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceMedlemmerTest {
    private String navident1 = "nav1";
    private String navident2 = "nav2";
    private RsBruker medlem1bruker = RsBrukerBuilder.builder().navIdent(navident1).build().convertToRealRsBruker();
    private RsBruker medlem2bruker = RsBrukerBuilder.builder().navIdent(navident2).build().convertToRealRsBruker();
    private List<String> navidenter = Arrays.asList(navident1, navident2);

    @Mock
    TeamRepository teamRepository;

    @Mock
    BrukerRepository brukerRepository;

    @Mock
    BrukerService brukerService;

    @Mock
    MapperFacade mapperFacade;

    @InjectMocks
    TeamService teamService;

    @Before
    public void setupMocks() {
        SecurityContextHolder.getContext().setAuthentication(new OidcTokenAuthentication("bruker",null, null, null));
    }

    @Test(expected = NotFoundException.class)
    public void fetchTeamById_kasterExceptionHvisTeamIkkeFunnet(){
        when(teamRepository.findTeamById(any())).thenReturn(null);
        teamService.fetchTeamById(1l);
    }

    @Test
    public void fetchTeamById_girTeamHvisTeamFinnesIDB(){
        when(teamRepository.findTeamById(any())).thenReturn(new Team());
        Team t = teamService.fetchTeamById(1l);
        assertThat(t, is(notNullValue()));
    }

    @Test
    public void opprettTeam_oppretterTeamBasertPaaArgumentInputOgLeggerTilBrukerSomEierOgMedlem() {
        RsOpprettTeam rt = new RsOpprettTeam();

        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>()).build().convertToRealTeam();
        Bruker b1 = BrukerBuilder.builder().navIdent("nav1").build().convertToRealBruker();

        when(mapperFacade.map(rt, Team.class)).thenReturn(t);
        when(brukerService.fetchBruker(any())).thenReturn(b1);

        teamService.opprettTeam(rt);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getEier(), is(b1));
        assertThat(savedTeam.getMedlemmer().contains(b1), is(true));
        assertThat(savedTeam.getDatoOpprettet(), is(notNullValue()));
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
        b1.setNavIdent(navident1);
        b2.setNavIdent(navident2);

        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>(Arrays.asList(b1, b2))).build().convertToRealTeam();

        when(teamRepository.findTeamById(any())).thenReturn(t);
        teamService.fjernMedlemmer(1l, Arrays.asList(navident1));

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(1));
        assertThat(savedTeam.getMedlemmer().contains(b2), is(true));
    }

    @Test
    public void deleteTeam_KallerRepositoryDeleteTeam(){
        Long id = 1l;
        teamService.deleteTeam(id);
        verify(teamRepository).deleteById(id);
    }

    private Team captureTheTeamSavedToRepo() {
        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

}