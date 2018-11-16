package no.nav.dolly.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerMedTeamsOgFavoritter;
import no.nav.dolly.domain.resultset.RsOpprettTestgruppe;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeMedErMedlemOgFavoritt;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.dolly.testdata.builder.RsOpprettTestgruppeBuilder;
import no.nav.dolly.testdata.builder.RsTeamBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeServiceTest {

    private static final long GROUP_ID = 1L;

    private static final String IDENT_ONE = "1";

    private static final String IDENT_TWO = "2";

    private String standardPrincipal = "princ";

    @Mock
    private GruppeRepository gruppeRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private TeamService teamService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private NonTransientDataAccessException nonTransientDataAccessException;

    @InjectMocks
    private TestgruppeService testgruppeService;

    private Testgruppe testGruppe;

    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(standardPrincipal, null, null, null)
        );
        when(nonTransientDataAccessException.getRootCause()).thenReturn(new Throwable());

        Set gruppe = Sets.newHashSet(
                Arrays.asList(
                        TestidentBuilder.builder().ident(IDENT_ONE).build().convertToRealTestident(),
                        TestidentBuilder.builder().ident(IDENT_TWO).build().convertToRealTestident()
                ));
        testGruppe = TestgruppeBuilder.builder().id(GROUP_ID).testidenter(gruppe).hensikt("test").build().convertToRealTestgruppe();
    }

    @Test
    public void opprettTestgruppe_HappyPath() {
        RsOpprettTestgruppe rsTestgruppe = Mockito.mock(RsOpprettTestgruppe.class);
        Team team = Mockito.mock(Team.class);
        Bruker bruker = Mockito.mock(Bruker.class);
        Testgruppe gruppe = new Testgruppe();
        Testgruppe savedGruppe = Mockito.mock(Testgruppe.class);

        when(teamService.fetchTeamOrOpprettBrukerteam(any())).thenReturn(team);
        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);
        when(mapperFacade.map(rsTestgruppe, Testgruppe.class)).thenReturn(gruppe);
        when(gruppeRepository.save(gruppe)).thenReturn(savedGruppe);

        testgruppeService.opprettTestgruppe(rsTestgruppe);

        ArgumentCaptor<Testgruppe> cap = ArgumentCaptor.forClass(Testgruppe.class);
        verify(gruppeRepository).save(cap.capture());

        Testgruppe res = cap.getValue();

        assertThat(res.getTeamtilhoerighet(), is(team));
        assertThat(res.getOpprettetAv(), is(bruker));
        assertThat(res.getSistEndretAv(), is(bruker));
    }

    @Test(expected = NotFoundException.class)
    public void fetchTestgruppeById_KasterExceptionHvisGruppeIkkeErFunnet() throws Exception {
        Optional<Testgruppe> op = Optional.empty();
        when(gruppeRepository.findById(any())).thenReturn(op);

        testgruppeService.fetchTestgruppeById(1l);
    }

    @Test
    public void fetchTestgruppeById_ReturnererGruppeHvisGruppeMedIdFinnes() throws Exception {
        Testgruppe g = Mockito.mock(Testgruppe.class);
        Optional<Testgruppe> op = Optional.of(g);
        when(gruppeRepository.findById(any())).thenReturn(op);

        Testgruppe hentetGruppe = testgruppeService.fetchTestgruppeById(1l);

        assertThat(g, is(hentetGruppe));
    }

    @Test
    public void fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker() {
        RsTestgruppe tg1 = RsTestgruppe.builder().id(1l).build();
        RsTestgruppe tg2 = RsTestgruppe.builder().id(2l).build();
        RsTestgruppe tg3 = RsTestgruppe.builder().id(3l).build();

        RsTeam t1 = RsTeamBuilder.builder()
                .grupper(new HashSet<>(Arrays.asList(tg3)))
                .build()
                .convertToRealRsTeam();

        RsBruker rBruker = RsBrukerBuilder.builder()
                .favoritter(new HashSet<>(Arrays.asList(tg1, tg2)))
                .navIdent(standardPrincipal)
                .build()
                .convertToRealRsBruker();

        RsBrukerMedTeamsOgFavoritter r = RsBrukerMedTeamsOgFavoritter.builder()
                .teams(new HashSet<>(Arrays.asList(t1)))
                .bruker(rBruker)
                .build();

        when(brukerService.getBrukerMedTeamsOgFavoritter(any())).thenReturn(r);

        Set<RsTestgruppe> grupper = testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(standardPrincipal);

        assertThat(grupper, hasItem(hasProperty("id", equalTo(1l))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(2l))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(3l))));
    }

    @Test
    public void getRsTestgruppeMedErMedlem_happyPath() {
        Testgruppe g1 = TestgruppeBuilder.builder().id(1l).build().convertToRealTestgruppe();
        Testgruppe g2 = TestgruppeBuilder.builder().id(2l).build().convertToRealTestgruppe();
        Testgruppe g3 = TestgruppeBuilder.builder().id(3l).build().convertToRealTestgruppe();
        Testgruppe g4 = TestgruppeBuilder.builder().id(4l).build().convertToRealTestgruppe();

        Team t1 = TeamBuilder.builder()
                .grupper(new HashSet<>(Arrays.asList(g3)))
                .navn("team")
                .build()
                .convertToRealTeam();

        RsTeamMedIdOgNavn rsT = new RsTeamMedIdOgNavn();
        rsT.setId(1l);
        rsT.setNavn("team");

        Bruker bruker = Bruker.builder()
                .favoritter(new HashSet(Arrays.asList(g1, g2)))
                .teams(new HashSet<>(Arrays.asList(t1)))
                .navIdent(standardPrincipal)
                .build();

        RsTestgruppeMedErMedlemOgFavoritt r = new RsTestgruppeMedErMedlemOgFavoritt();
        r.setId(1l);
        r.setTeam(rsT);

        HashSet gr = new HashSet(Arrays.asList(r));

        HashSet grupper = new HashSet(Arrays.asList(g4));

        when(mapperFacade.mapAsSet(grupper, RsTestgruppeMedErMedlemOgFavoritt.class)).thenReturn(gr);
        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);

        testgruppeService.getRsTestgruppeMedErMedlem(grupper);

        verify(mapperFacade).mapAsSet(grupper, RsTestgruppeMedErMedlemOgFavoritt.class);
    }

    @Test
    public void getRsTestgruppeMedErMedlem_happyPathTwoArgs() {
        Testgruppe g1 = TestgruppeBuilder.builder().id(1l).build().convertToRealTestgruppe();
        Testgruppe g2 = TestgruppeBuilder.builder().id(2l).build().convertToRealTestgruppe();
        Testgruppe g3 = TestgruppeBuilder.builder().id(3l).build().convertToRealTestgruppe();
        Testgruppe g4 = TestgruppeBuilder.builder().id(4l).build().convertToRealTestgruppe();

        Team t1 = TeamBuilder.builder()
                .grupper(new HashSet<>(Arrays.asList(g3)))
                .navn("team")
                .build()
                .convertToRealTeam();

        RsTeamMedIdOgNavn rsT = new RsTeamMedIdOgNavn();
        rsT.setId(1l);
        rsT.setNavn("team");

        Bruker bruker = Bruker.builder()
                .favoritter(new HashSet(Arrays.asList(g1, g2)))
                .teams(new HashSet<>(Arrays.asList(t1)))
                .navIdent(standardPrincipal)
                .build();

        RsTestgruppeMedErMedlemOgFavoritt r = new RsTestgruppeMedErMedlemOgFavoritt();
        r.setId(1l);
        r.setTeam(rsT);

        HashSet gr = new HashSet(Arrays.asList(r));
        HashSet grupper = new HashSet(Arrays.asList(g4));

        when(mapperFacade.mapAsSet(grupper, RsTestgruppeMedErMedlemOgFavoritt.class)).thenReturn(gr);
        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);

        Set<RsTestgruppeMedErMedlemOgFavoritt> res = testgruppeService.getRsTestgruppeMedErMedlem(grupper, standardPrincipal);

        assertThat(res.size(), is(1));
        assertThat(res, hasItem(hasProperty("id", equalTo(1l))));
    }

    @Test
    public void saveGruppeTilDB_returnererTestgruppeHvisTestgruppeFinnes() {
        Testgruppe g = new Testgruppe();
        when(gruppeRepository.save(any())).thenReturn(g);

        Testgruppe res = testgruppeService.saveGruppeTilDB(new Testgruppe());
        assertThat(res, is(notNullValue()));
    }

    @Test
    public void slettGruppeById_deleteBlirKaltMotRepoMedGittId() {
        testgruppeService.slettGruppeById(GROUP_ID);
        verify(gruppeRepository).deleteTestgruppeById(GROUP_ID);
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveGruppeTilDB_kasterExceptionHvisDBConstraintErBrutt() {
        when(gruppeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        testgruppeService.saveGruppeTilDB(new Testgruppe());
    }

    @Test(expected = DollyFunctionalException.class)
    public void saveGruppeTilDB_kasterDollyExceptionHvisDBConstraintErBrutt() throws Exception {
        when(gruppeRepository.save(any())).thenThrow(nonTransientDataAccessException);
        testgruppeService.saveGruppeTilDB(new Testgruppe());
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveGrupper_kasterExceptionHvisDBConstraintErBrutt() {
        when(gruppeRepository.saveAll(any())).thenThrow(DataIntegrityViolationException.class);
        testgruppeService.saveGrupper(new HashSet<>(Arrays.asList(new Testgruppe())));
    }

    @Test(expected = DollyFunctionalException.class)
    public void saveGrupper_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(gruppeRepository.saveAll(any())).thenThrow(nonTransientDataAccessException);
        testgruppeService.saveGrupper(new HashSet<>(Arrays.asList(new Testgruppe())));
    }

    @Test(expected = NotFoundException.class)
    public void fetchGrupperByIdsIn_kasterExceptionOmGruppeIkkeFinnes() {
        testgruppeService.fetchGrupperByIdsIn(Arrays.asList(anyLong()));
    }

    @Test(expected = NotFoundException.class)
    public void fetchIdenterByGruppeId_kasterException() {
        when(gruppeRepository.findById(anyLong())).thenReturn(Optional.empty());
        testgruppeService.fetchIdenterByGruppeId(1L);
    }

    @Test
    public void fetchIdenterByGruppeId_gruppeTilIdentString() {
        when(gruppeRepository.findById(GROUP_ID)).thenReturn(Optional.of(testGruppe));

        List<String> identer = testgruppeService.fetchIdenterByGruppeId(GROUP_ID);
        assertThat(identer.contains(IDENT_ONE), is(true));
        assertThat(identer.contains(IDENT_TWO), is(true));
        assertThat(identer.size(), is(2));
    }

    @Test
    public void fetchIdenterByGroupId_sjekkTommeGrupper() {
        Testgruppe tg = TestgruppeBuilder.builder().id(GROUP_ID).testidenter(new HashSet<>()).build().convertToRealTestgruppe();

        when(gruppeRepository.findById(GROUP_ID)).thenReturn(Optional.of(tg));
        List<String> identer = testgruppeService.fetchIdenterByGruppeId(GROUP_ID);

        assertThat(identer.size(), is(0));
    }

    @Test
    public void oppdaterTestgruppe_sjekkAtDBKalles() {
        long teamId = 2L;

        RsOpprettTestgruppe rsOpprettTestgruppe = RsOpprettTestgruppeBuilder.builder().hensikt("test").navn("navn").teamId(1L).build().convertToRealRsOpprettTestgruppe();

        Team team = TeamBuilder.builder().navn("team").id(teamId).build().convertToRealTeam();

        when(gruppeRepository.findById(anyLong())).thenReturn(Optional.of(testGruppe));
        when(brukerService.fetchBruker(anyString())).thenReturn(new Bruker("navIdent"));
        doReturn(team).when(teamService).fetchTeamById(anyLong());
        when(mapperFacade.map(rsOpprettTestgruppe, Testgruppe.class)).thenReturn(testGruppe);
        testgruppeService.oppdaterTestgruppe(GROUP_ID, rsOpprettTestgruppe);
        verify(gruppeRepository).save(testGruppe);
    }
}