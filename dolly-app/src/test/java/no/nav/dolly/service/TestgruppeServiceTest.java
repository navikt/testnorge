package no.nav.dolly.service;

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
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestGruppeRepository;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.dolly.testdata.builder.RsTeamBuilder;
import no.nav.dolly.testdata.builder.RsTestgruppeBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeServiceTest {

    private String standardPrincipal = "princ";

    @Mock
    private TestGruppeRepository testGruppeRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private TeamService teamService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private TestgruppeService testgruppeService;

    @Before
    public void setup(){
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(standardPrincipal,null, null, null)
        );
    }

    @Test
    public void opprettTestgruppe_HappyPath(){
        RsOpprettTestgruppe rsTestgruppe = Mockito.mock(RsOpprettTestgruppe.class);
        Team team = Mockito.mock(Team.class);
        Bruker bruker = Mockito.mock(Bruker.class);
        Testgruppe gruppe = new Testgruppe();
        Testgruppe savedGruppe = Mockito.mock(Testgruppe.class);

        when(teamService.fetchTeamById(any())).thenReturn(team);
        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);
        when(mapperFacade.map(rsTestgruppe, Testgruppe.class)).thenReturn(gruppe);
        when(testGruppeRepository.save(gruppe)).thenReturn(savedGruppe);

        testgruppeService.opprettTestgruppe(rsTestgruppe);

        ArgumentCaptor<Testgruppe> cap = ArgumentCaptor.forClass(Testgruppe.class);
        verify(testGruppeRepository).save(cap.capture());

        Testgruppe res = cap.getValue();

        assertThat(res.getTeamtilhoerighet(), is(team));
        assertThat(res.getOpprettetAv(), is(bruker));
        assertThat(res.getSistEndretAv(), is(bruker));
    }

    @Test(expected = NotFoundException.class)
    public void fetchTestgruppeById_KasterExceptionHvisGruppeIkkeErFunnet() throws Exception {
        Optional<Testgruppe> op = Optional.empty();
        when(testGruppeRepository.findById(any())).thenReturn(op);

        testgruppeService.fetchTestgruppeById(1l);
    }

    @Test
    public void fetchTestgruppeById_ReturnererGruppeHvisGruppeMedIdFinnes() throws Exception {
        Testgruppe g = Mockito.mock(Testgruppe.class);
        Optional<Testgruppe> op = Optional.of(g);
        when(testGruppeRepository.findById(any())).thenReturn(op);

        Testgruppe hentetGruppe = testgruppeService.fetchTestgruppeById(1l);

        assertThat(g, is(hentetGruppe));
    }

    @Test
    public void fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(){
        RsTestgruppe tg1 = RsTestgruppeBuilder.builder().id(1l).build().convertToRealRsTestgruppe();
        RsTestgruppe tg2 = RsTestgruppeBuilder.builder().id(2l).build().convertToRealRsTestgruppe();
        RsTestgruppe tg3 = RsTestgruppeBuilder.builder().id(3l).build().convertToRealRsTestgruppe();

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

        Set<RsTestgruppe> grupper =  testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(standardPrincipal);

        assertThat(grupper, hasItem(hasProperty("id", equalTo(1l))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(2l))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(3l))));
    }

    @Test
    public void getRsTestgruppeMedErMedlem_happyPath(){
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

        Bruker bruker = BrukerBuilder.builder()
                .favoritter(new HashSet(Arrays.asList(g1, g2)))
                .teams(new HashSet<>(Arrays.asList(t1)))
                .navIdent(standardPrincipal)
                .build()
                .convertToRealBruker();

        RsTestgruppeMedErMedlemOgFavoritt r =  new RsTestgruppeMedErMedlemOgFavoritt();
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
    public void getRsTestgruppeMedErMedlem_happyPathTwoArgs(){
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

        Bruker bruker = BrukerBuilder.builder()
                .favoritter(new HashSet(Arrays.asList(g1, g2)))
                .teams(new HashSet<>(Arrays.asList(t1)))
                .navIdent(standardPrincipal)
                .build()
                .convertToRealBruker();

        RsTestgruppeMedErMedlemOgFavoritt r =  new RsTestgruppeMedErMedlemOgFavoritt();
        r.setId(1l);
        r.setTeam(rsT);

        HashSet gr = new HashSet(Arrays.asList(r));
        HashSet grupper = new HashSet(Arrays.asList(g4));

        when(mapperFacade.mapAsSet(grupper, RsTestgruppeMedErMedlemOgFavoritt.class)).thenReturn(gr);
        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);

        Set<RsTestgruppeMedErMedlemOgFavoritt> res =  testgruppeService.getRsTestgruppeMedErMedlem(grupper, standardPrincipal);

        assertThat(res.size(), is(1));
        assertThat(res, hasItem(hasProperty("id", equalTo(1l))));
    }

    @Test
    public void saveGruppeTilDB_returnererTestgruppeHvisTestgruppeFinnes(){
        Testgruppe g = new Testgruppe();
        when(testGruppeRepository.save(any())).thenReturn(g);

        Testgruppe res = testgruppeService.saveGruppeTilDB(new Testgruppe());
        assertThat(res, is(notNullValue()));
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveGruppeTilDB_kasterExceptionHvisDBConstraintErBrutt(){
        when(testGruppeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        testgruppeService.saveGruppeTilDB(new Testgruppe());
    }

}