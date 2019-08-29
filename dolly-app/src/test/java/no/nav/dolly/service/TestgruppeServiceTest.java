package no.nav.dolly.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeServiceTest {

    private static final long GROUP_ID = 1L;
    private static final long TEAM_ID = 11L;
    private static final String IDENT_ONE = "1";
    private static final String IDENT_TWO = "2";
    private static final String standardPrincipal = "PRINC";

    @Mock
    private GruppeRepository gruppeRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private TeamService teamService;

    @Mock
    private IdentService identService;

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

        Set gruppe = newHashSet(
                asList(
                        TestidentBuilder.builder().ident(IDENT_ONE).build().convertToRealTestident(),
                        TestidentBuilder.builder().ident(IDENT_TWO).build().convertToRealTestident()
                ));
        testGruppe = Testgruppe.builder().id(GROUP_ID).testidenter(gruppe).hensikt("test").build();
    }

    @Test
    public void opprettTestgruppe_HappyPath() {
        RsOpprettEndreTestgruppe rsTestgruppe = mock(RsOpprettEndreTestgruppe.class);
        Team team = mock(Team.class);
        Bruker bruker = mock(Bruker.class);

        when(teamService.fetchTeamOrOpprettBrukerteam(any())).thenReturn(team);
        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);

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

        testgruppeService.fetchTestgruppeById(1L);
    }

    @Test
    public void fetchTestgruppeById_ReturnererGruppeHvisGruppeMedIdFinnes() throws Exception {
        Testgruppe g = mock(Testgruppe.class);
        Optional<Testgruppe> op = Optional.of(g);
        when(gruppeRepository.findById(any())).thenReturn(op);

        Testgruppe hentetGruppe = testgruppeService.fetchTestgruppeById(1L);

        assertThat(g, is(hentetGruppe));
    }

    @Test
    public void fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker() {
        Testgruppe tg1 = Testgruppe.builder().id(1L).navn("test1").build();
        Testgruppe tg2 = Testgruppe.builder().id(2L).navn("test2").build();
        Testgruppe tg3 = Testgruppe.builder().id(3L).navn("test3").build();

        Team t1 = Team.builder()
                .grupper(singletonList(tg3))
                .build();

        Bruker bruker = Bruker.builder()
                .favoritter(newHashSet(asList(tg1, tg2)))
                .teams(newHashSet(singletonList(t1)))
                .navIdent(standardPrincipal)
                .build();

        when(brukerService.fetchBruker(any())).thenReturn(bruker);

        List<Testgruppe> grupper = testgruppeService.fetchTestgrupperByNavIdent(standardPrincipal);

        assertThat(grupper, hasItem(hasProperty("id", equalTo(1L))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(2L))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(3L))));
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
        verify(brukerService).sletteBrukerFavoritterByGroupId(GROUP_ID);
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
        testgruppeService.saveGrupper(newHashSet(singletonList(new Testgruppe())));
    }

    @Test(expected = DollyFunctionalException.class)
    public void saveGrupper_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(gruppeRepository.saveAll(any())).thenThrow(nonTransientDataAccessException);
        testgruppeService.saveGrupper(newHashSet(singletonList(new Testgruppe())));
    }

    @Test(expected = NotFoundException.class)
    public void fetchGrupperByIdsIn_kasterExceptionOmGruppeIkkeFinnes() {
        testgruppeService.fetchGrupperByIdsIn(singletonList(anyLong()));
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
        Testgruppe tg = Testgruppe.builder().id(GROUP_ID).testidenter(new HashSet<>()).build();

        when(gruppeRepository.findById(GROUP_ID)).thenReturn(Optional.of(tg));
        List<String> identer = testgruppeService.fetchIdenterByGruppeId(GROUP_ID);

        assertThat(identer.size(), is(0));
    }

    @Test
    public void oppdaterTestgruppe_sjekkAtDBKalles() {
        long teamId = 2L;

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder().hensikt("test").navn("navn").teamId(1L).build();

        Team team = Team.builder().navn("team").id(teamId).build();

        when(gruppeRepository.findById(anyLong())).thenReturn(Optional.of(testGruppe));
        when(brukerService.fetchBruker(anyString())).thenReturn(new Bruker("navIdent"));
        doReturn(team).when(teamService).fetchTeamById(anyLong());
        testgruppeService.oppdaterTestgruppe(GROUP_ID, rsOpprettEndreTestgruppe);
        verify(gruppeRepository).save(testGruppe);
    }

    @Test
    public void slettGruppeByTeamId() {

        testgruppeService.slettGruppeByTeamId(TEAM_ID);
        verify(identService).slettTestidenterByTeamId(TEAM_ID);
        verify(brukerService).sletteBrukerFavoritterByTeamId(TEAM_ID);
        verify(gruppeRepository).deleteTestgruppeByTeamtilhoerighetId(TEAM_ID);
    }

    @Test
    public void getTestgruppeByNavidentOgTeamId() {
        testgruppeService.getTestgruppeByNavidentOgTeamId(IDENT_ONE, TEAM_ID);
        verify(gruppeRepository).findAllByTeamtilhoerighetOrderByNavn(any(Team.class));
    }

    @Test
    public void getTestgrupper() {
        testgruppeService.getTestgruppeByNavidentOgTeamId(null, null);
        verify(gruppeRepository).findAllByOrderByNavn();
    }
}