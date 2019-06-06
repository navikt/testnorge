package no.nav.dolly.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static no.nav.dolly.util.ListUtil.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    private static final String CURRENT_BRUKER_IDENT = "NAV1";
    private static final String NAVIDENT_2 = "nav2";
    private static final Long TEAM_ID = 11L;
    private static final List<String> navidenter = asList(CURRENT_BRUKER_IDENT, NAVIDENT_2);
    private static final Optional<Team> tomOptional = empty();

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private NonTransientDataAccessException nonTransientDataAccessException;

    @Mock
    private TestgruppeService testgruppeService;

    @InjectMocks
    private TeamService teamService;

    private static Authentication authentication;

    @Before
    public void setupMocks() {
        when(nonTransientDataAccessException.getRootCause()).thenReturn(new Throwable());
    }

    @BeforeClass
    public static void beforeClass() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(CURRENT_BRUKER_IDENT, null, null, null));
    }

    @AfterClass
    public static void afterClass() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test(expected = NotFoundException.class)
    public void fetchTeamById_kasterExceptionHvisTeamIkkeFunnet() {
        when(teamRepository.findById(any())).thenReturn(tomOptional);
        teamService.fetchTeamById(1L);
    }

    @Test
    public void fetchTeamById_girTeamHvisTeamFinnesIDB() {
        Optional<Team> opMedTeam = of(new Team());
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        Team t = teamService.fetchTeamById(1L);
        assertThat(t, is(notNullValue()));
    }

    @Test
    public void opprettTeam_oppretterTeamBasertPaaArgumentInputOgLeggerTilBrukerSomEierOgMedlem() {
        RsOpprettTeam rt = new RsOpprettTeam();

        Bruker b1 = Bruker.builder().navIdent("nav1").build();

        when(brukerService.fetchBruker(any())).thenReturn(b1);

        teamService.opprettTeam(rt);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getEier(), is(b1));
        assertThat(savedTeam.getMedlemmer().contains(b1), is(true));
        assertThat(savedTeam.getDatoOpprettet(), is(notNullValue()));
    }

    @Test
    public void addMedlemmer_LeggerTilMedlemmerITeam() {
        Team t = Team.builder().navn("t").build();

        RsBruker rb1 = RsBruker.builder().navIdent("nav1").build();
        RsBruker rb2 = RsBruker.builder().navIdent("nav2").build();
        List<RsBruker> inputBrukere = asList(rb1, rb2);

        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent("nav1");
        b2.setNavIdent("nav2");

        Optional<Team> opMedTeam = of(t);
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        when(mapperFacade.mapAsList(inputBrukere, Bruker.class)).thenReturn(asList(b1, b2));

        teamService.addMedlemmer(1L, inputBrukere);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(2));
    }

    @Test
    public void fetchTeamOrOpprettBrukerteam_oppretterNyttTeamMedCurrentBrukerSomNavnHvisIngenTeamIdGitt() {
        when(teamRepository.findByNavn(anyString())).thenReturn(empty());

        teamService.fetchTeamOrOpprettBrukerteam(null);
        Team t = captureTheTeamSavedToRepo();

        assertThat(t.getNavn(), is(CURRENT_BRUKER_IDENT));
    }

    @Test
    public void fetchTeamOrOpprettBrukerteam_hvisTeamIdErGittProverAaFinneTeamMedId() {
        when(teamRepository.findById(1L)).thenReturn(of(new Team()));
        teamService.fetchTeamOrOpprettBrukerteam(1L);
        verify(teamRepository).findById(1L);
    }

    @Test
    public void addMedlemmerByNavidenter_BrukereBasertPaaIdenterBlirLagtTilITeam() {
        Team t = Team.builder().navn("t").build();

        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent("nav1");
        b2.setNavIdent("nav2");

        Optional<Team> opMedTeam = of(t);
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        when(brukerRepository.findByNavIdentInOrderByNavIdent(navidenter)).thenReturn(asList(b1, b2));

        teamService.addMedlemmerByNavidenter(1L, navidenter);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(2));
    }

    @Test
    public void fjernMedlemmerHvisMedlemHarSammeNavidentSomInput() {
        Bruker b1 = Bruker.builder().navIdent(CURRENT_BRUKER_IDENT).build();
        Bruker b2 = Bruker.builder().navIdent(NAVIDENT_2).build();

        Team team = Team.builder().navn("t").medlemmer(listOf(b1, b2)).build();

        Optional<Team> opMedTeam = of(team);
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        teamService.fjernMedlemmer(TEAM_ID, listOf(CURRENT_BRUKER_IDENT));

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(1));
        assertThat(savedTeam.getMedlemmer().contains(b2), is(true));
    }

    @Test(expected = NotFoundException.class)
    public void slettMedlemNotFound() {
        teamService.slettMedlem(TEAM_ID, CURRENT_BRUKER_IDENT);
    }

    @Test
    public void slettMedlemDeleteOk() {
        when(teamRepository.findById(TEAM_ID)).thenReturn(of(
                Team.builder()
                        .id(TEAM_ID)
                        .medlemmer(listOf(Bruker.builder()
                                .navIdent(CURRENT_BRUKER_IDENT)
                                .build()))
                        .build()));
        teamService.slettMedlem(TEAM_ID, CURRENT_BRUKER_IDENT);
        verify(teamRepository).save(any(Team.class));
    }

    @Test(expected = NotFoundException.class)
    public void updateTeamNotFound() {
        teamService.updateTeamInfo(TEAM_ID, RsTeamUtvidet.builder().medlemmer(singletonList(RsBruker.builder().navIdent(CURRENT_BRUKER_IDENT).build())).build());
    }

    @Test
    public void updateTeamOk() {
        Team team = Team.builder().medlemmer(singletonList(Bruker.builder().navIdent(CURRENT_BRUKER_IDENT).build())).build();
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);
        teamService.updateTeamInfo(TEAM_ID, RsTeamUtvidet.builder().medlemmer(singletonList(RsBruker.builder().navIdent(CURRENT_BRUKER_IDENT).build())).build());
        verify(teamRepository).save(team);
    }

    @Test
    public void deleteTeam_KallerRepositoryDeleteTeam() {
        teamService.deleteTeam(TEAM_ID);
        verify(testgruppeService).slettGruppeByTeamId(TEAM_ID);
        verify(teamRepository).deleteTeamById(TEAM_ID);
    }

    private Team captureTheTeamSavedToRepo() {
        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveTeamToDB_DataIntegrityViolationExceptionCatches() {
        when(teamRepository.save(any(Team.class))).thenThrow(DataIntegrityViolationException.class);
        teamService.saveTeamToDB(new Team());
    }

    @Test(expected = DollyFunctionalException.class)
    public void saveTeamToDB_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(teamRepository.save(any(Team.class))).thenThrow(nonTransientDataAccessException);
        teamService.saveTeamToDB(new Team());
    }

    @Test
    public void fetchTeamsByMedlemskapInTeams_ok() {

        String navIdent = "1";

        teamService.fetchTeamsByMedlemskapInTeams(navIdent);

        verify(teamRepository).findByMedlemmerNavIdentOrderByNavn(navIdent);
    }
}