package no.nav.dolly.service;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerMedTeamsOgFavoritter;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.dolly.testdata.builder.RsTeamBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class BrukerServiceTest {

    private final static String navIdent = "bruker";

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private TestgruppeService gruppeService;

    @InjectMocks
    private BrukerService service;

    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new OidcTokenAuthentication(navIdent, null, null, null));
    }

    @Test
    public void opprettBruker_kallerRepositorySave() {
        RsBruker b = new RsBruker();
        service.opprettBruker(new RsBruker());
        verify(brukerRepository).save(any());
    }

    @Test
    public void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerErFunnet() {
        when(brukerRepository.findBrukerByNavIdent(any())).thenReturn(new Bruker());
        Bruker b = service.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void fetchBruker_kasterExceptionHvisIngenBrukerFunnet() {
        when(brukerRepository.findBrukerByNavIdent(any())).thenReturn(null);
        Bruker b = service.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void getBruker_KallerRepoHentBrukere() {
        service.fetchBrukere();
        verify(brukerRepository).findAll();
    }

    //funker ikke
    @Test
    public void fetchOrCreateBruker_saveKallesVedNotFoundException() {
        service.fetchOrCreateBruker(null);
        verify(brukerRepository).save(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveBrukerTilDB_kasterExceptionNarDBConstrainBrytes() {
        when(brukerRepository.save(any(Bruker.class))).thenThrow(DataIntegrityViolationException.class);
        service.saveBrukerTilDB(new Bruker());
    }

    @Test
    public void getBrukerMedTeamsOgFavoritter_setterBrukerOgDensTeamIReturnertObjekt() {
        String navident = "navident";

        Bruker bruker = BrukerBuilder.builder().navIdent(navident).build().convertToRealBruker();
        RsBruker rsBruker = RsBrukerBuilder.builder().navIdent(navident).build().convertToRealRsBruker();

        Team team = TeamBuilder.builder().navn("navteam").eier(bruker).build().convertToRealTeam();
        RsTeam rsTeam = RsTeamBuilder.builder().navn("navteam").eierNavIdent(navident).build().convertToRealRsTeam();
        Set teamSet = new HashSet(Arrays.asList(rsTeam));
        List<Team> teamList = Arrays.asList(team);

        when(brukerRepository.findBrukerByNavIdent(navident)).thenReturn(bruker);
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);
        when(teamService.fetchTeamsByMedlemskapInTeams(navident)).thenReturn(teamList);
        when(mapperFacade.mapAsSet(teamList, RsTeam.class)).thenReturn(teamSet);

        RsBrukerMedTeamsOgFavoritter res = service.getBrukerMedTeamsOgFavoritter(navident);

        assertThat(res.getBruker(), is(rsBruker));
        assertThat(res.getTeams().size(), is(1));
    }

    @Test
    public void addFavoritter_kallerSaveBrukerMedLagtTilFavoritter() {
        Testgruppe gruppe = TestgruppeBuilder.builder().navn("g1").hensikt("hen").build().convertToRealTestgruppe();
        List<Testgruppe> gruppeList = Arrays.asList(gruppe);

        String navident = "navident";
        Bruker bruker = BrukerBuilder.builder().navIdent(navident).favoritter(new HashSet<>()).build().convertToRealBruker();

        when(brukerRepository.findBrukerByNavIdent(navident)).thenReturn(bruker);
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker endretBruker = service.leggTilFavoritter(navident, gruppeList);

        assertThat(endretBruker.getFavoritter().size(), is(1));
        assertThat(endretBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("g1"))).and(
                hasProperty("hensikt", equalTo("hen"))
        )));
    }

    @Test
    public void leggTilFavoritter_medGrupperIDer() {
        List<Long> ider = Arrays.asList(1l, 2l);
        Testgruppe g = TestgruppeBuilder.builder().navn("gruppe").hensikt("hen").build().convertToRealTestgruppe();
        List<Testgruppe> grupper = Arrays.asList(g);
        Bruker b = BrukerBuilder.builder().navIdent(navIdent).favoritter(new HashSet<>()).teams(new HashSet<>()).build().convertToRealBruker();

        when(gruppeService.fetchGrupperByIdsIn(ider)).thenReturn(grupper);
        when(brukerRepository.findBrukerByNavIdent(navIdent)).thenReturn(b);
        when(brukerRepository.save(b)).thenReturn(b);

        Bruker hentetBruker = service.leggTilFavoritter(ider);

        verify(brukerRepository).save(b);

        assertThat(hentetBruker, is(b));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe"))).and(
                hasProperty("hensikt", equalTo("hen"))
        )));
    }

    @Test
    public void fjernFavoritter_medGrupperIDer() {
        List<Long> ider = Arrays.asList(1l);
        Testgruppe g = TestgruppeBuilder.builder().navn("gruppe").hensikt("hen").build().convertToRealTestgruppe();
        Testgruppe g2 = TestgruppeBuilder.builder().navn("gruppe2").hensikt("hen2").build().convertToRealTestgruppe();
        List<Testgruppe> grupperSomSkalBort = Arrays.asList(g);
        Set<Testgruppe> favoritter = new HashSet<>();
        favoritter.add(g);
        favoritter.add(g2);

        Bruker b = BrukerBuilder.builder().navIdent(navIdent).favoritter(favoritter).teams(new HashSet<>()).build().convertToRealBruker();
        g.setFavorisertAv(new HashSet<>(Arrays.asList(b)));
        g2.setFavorisertAv(new HashSet<>(Arrays.asList(b)));

        when(gruppeService.fetchGrupperByIdsIn(ider)).thenReturn(grupperSomSkalBort);
        when(brukerRepository.findBrukerByNavIdent(navIdent)).thenReturn(b);
        when(brukerRepository.save(b)).thenReturn(b);

        Bruker hentetBruker = service.fjernFavoritter(ider);

        verify(brukerRepository).save(b);

        assertThat(hentetBruker, is(b));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe2"))).and(
                hasProperty("hensikt", equalTo("hen2"))
        )));

        assertThat(g.getFavorisertAv().isEmpty(), is(true));
    }

}