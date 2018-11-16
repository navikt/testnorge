package no.nav.dolly.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {
    private static final String currentBrukerIdent = "nav1";
    private String navident2 = "nav2";
    private List<String> navidenter = Arrays.asList(currentBrukerIdent, navident2);
    private Optional<Team> tomOptional = Optional.empty();

    @Mock
    TeamRepository teamRepository;

    @Mock
    BrukerRepository brukerRepository;

    @Mock
    BrukerService brukerService;

    @Mock
    MapperFacade mapperFacade;

    @Mock
    private NonTransientDataAccessException nonTransientDataAccessException;

    @InjectMocks
    TeamService teamService;

    private static Authentication authentication;

    @Before
    public void setupMocks() {
        when(nonTransientDataAccessException.getRootCause()).thenReturn(new Throwable());
    }

    @BeforeClass
    public static void beforeClass() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(currentBrukerIdent, null, null, null)
        );
    }

    @AfterClass
    public static void afterClass() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test(expected = NotFoundException.class)
    public void fetchTeamById_kasterExceptionHvisTeamIkkeFunnet() {
        when(teamRepository.findById(any())).thenReturn(tomOptional);
        teamService.fetchTeamById(1l);
    }

    @Test
    public void fetchTeamById_girTeamHvisTeamFinnesIDB() {
        Optional<Team> opMedTeam = Optional.of(new Team());
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        Team t = teamService.fetchTeamById(1l);
        assertThat(t, is(notNullValue()));
    }

    @Test
    public void opprettTeam_oppretterTeamBasertPaaArgumentInputOgLeggerTilBrukerSomEierOgMedlem() {
        RsOpprettTeam rt = new RsOpprettTeam();

        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>()).build().convertToRealTeam();
        Bruker b1 = Bruker.builder().navIdent("nav1").build();

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

        Optional<Team> opMedTeam = Optional.of(t);
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        when(mapperFacade.mapAsList(inputBrukere, Bruker.class)).thenReturn(Arrays.asList(b1, b2));

        teamService.addMedlemmer(1l, inputBrukere);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(2));
    }

    @Test
    public void fetchTeamOrOpprettBrukerteam_oppretterNyttTeamMedCurrentBrukerSomNavnHvisIngenTeamIdGitt() throws Exception {
        when(teamRepository.findByNavn(anyString())).thenReturn(Optional.empty());

        teamService.fetchTeamOrOpprettBrukerteam(null);
        Team t = captureTheTeamSavedToRepo();

        assertThat(t.getNavn(), is(currentBrukerIdent));
    }

    @Test
    public void fetchTeamOrOpprettBrukerteam_hvisTeamIdErGittProverAaFinneTeamMedId() throws Exception {
        when(teamRepository.findById(1l)).thenReturn(Optional.of(new Team()));
        teamService.fetchTeamOrOpprettBrukerteam(1l);
        verify(teamRepository).findById(1l);
    }

    @Test
    public void addMedlemmerByNavidenter_BrukereBasertPaaIdenterBlirLagtTilITeam() {
        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>()).build().convertToRealTeam();

        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent("nav1");
        b2.setNavIdent("nav2");

        Optional<Team> opMedTeam = Optional.of(t);
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        when(brukerRepository.findByNavIdentIn(navidenter)).thenReturn(Arrays.asList(b1, b2));

        teamService.addMedlemmerByNavidenter(1l, navidenter);

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(2));
    }

    @Test
    public void fjernMedlemmer_fjernerMedlemHvisMedlemHarSammeNavidentSominput() {
        Bruker b1 = new Bruker();
        Bruker b2 = new Bruker();
        b1.setNavIdent(currentBrukerIdent);
        b2.setNavIdent(navident2);

        Team t = TeamBuilder.builder().navn("t").medlemmer(new HashSet<>(Arrays.asList(b1, b2))).build().convertToRealTeam();

        Optional<Team> opMedTeam = Optional.of(t);
        when(teamRepository.findById(any())).thenReturn(opMedTeam);
        teamService.fjernMedlemmer(1l, Arrays.asList(currentBrukerIdent));

        Team savedTeam = captureTheTeamSavedToRepo();

        assertThat(savedTeam.getMedlemmer().size(), is(1));
        assertThat(savedTeam.getMedlemmer().contains(b2), is(true));
    }

    @Test
    public void deleteTeam_KallerRepositoryDeleteTeam() {
        Long id = 1l;
        teamService.deleteTeam(id);
        verify(teamRepository).deleteById(id);
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
}