package no.nav.dolly.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.BrukerMedTeamsOgFavoritter;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.testdata.builder.RsTeamUtvidetBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class BrukerServiceTest {

    private final static String navIdent = "BRUKER";

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private TestgruppeService gruppeService;

    @InjectMocks
    private BrukerService brukerService;

    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new OidcTokenAuthentication(navIdent, null, null, null));
    }

    @Test
    public void opprettBruker_kallerRepositorySave() {
        brukerService.opprettBruker(new RsBruker());
        verify(brukerRepository).save(any());
    }

    @Test
    public void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerErFunnet() {
        when(brukerRepository.findBrukerByNavIdent(any())).thenReturn(new Bruker());
        Bruker b = brukerService.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void fetchBruker_kasterExceptionHvisIngenBrukerFunnet() {
        when(brukerRepository.findBrukerByNavIdent(any())).thenReturn(null);
        Bruker b = brukerService.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void getBruker_KallerRepoHentBrukere() {
        brukerService.fetchBrukere();
        verify(brukerRepository).findAllByOrderByNavIdent();
    }

    @Test
    public void fetchOrCreateBruker_saveKallesVedNotFoundException() {
        brukerService.fetchOrCreateBruker("tullestring");
        verify(brukerRepository).save(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveBrukerTilDB_kasterExceptionNarDBConstrainBrytes() {
        when(brukerRepository.save(any(Bruker.class))).thenThrow(DataIntegrityViolationException.class);
        brukerService.saveBrukerTilDB(new Bruker());
    }

    @Test
    public void getBrukerMedTeamsOgFavoritter_setterBrukerOgDensTeamIReturnertObjekt() {
        String navident = "NAVIDENT";

        Bruker bruker = Bruker.builder().navIdent(navident).build();

        Team team = Team.builder().navn("navteam").eier(bruker).build();
        List<Team> teamList = singletonList(team);

        when(brukerRepository.findBrukerByNavIdent(navident)).thenReturn(bruker);
        when(teamService.fetchTeamsByMedlemskapInTeams(navident)).thenReturn(teamList);

        BrukerMedTeamsOgFavoritter res = brukerService.getBrukerMedTeamsOgFavoritter(navident);

        assertThat(res.getBruker(), is(bruker));
        assertThat(res.getTeams().size(), is(1));
    }

    @Test
    public void leggTilFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        Bruker bruker = Bruker.builder().navIdent(navIdent).favoritter(new HashSet<>()).teams(new HashSet<>()).build();

        when(gruppeService.fetchTestgruppeById(ID)).thenReturn(testgruppe);
        when(brukerRepository.findBrukerByNavIdent(navIdent)).thenReturn(bruker);
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker hentetBruker = brukerService.leggTilFavoritt(ID);

        verify(brukerRepository).save(bruker);

        assertThat(hentetBruker, is(bruker));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe"))).and(
                hasProperty("hensikt", equalTo("hen"))
        )));
    }

    @Test
    public void fjernFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        Testgruppe testgruppe2 = Testgruppe.builder().navn("gruppe2").hensikt("hen2").build();
        Set<Testgruppe> favoritter = newHashSet(asList(testgruppe, testgruppe2));

        Bruker bruker = Bruker.builder().navIdent(navIdent).favoritter(favoritter).teams(new HashSet<>()).build();
        testgruppe.setFavorisertAv(newHashSet(singletonList(bruker)));
        testgruppe2.setFavorisertAv(newHashSet(singletonList(bruker)));

        when(gruppeService.fetchTestgruppeById(ID)).thenReturn(testgruppe);
        when(brukerRepository.findBrukerByNavIdent(navIdent)).thenReturn(bruker);
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker hentetBruker = brukerService.fjernFavoritt(ID);

        verify(brukerRepository).save(bruker);

        assertThat(hentetBruker, is(bruker));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe2"))).and(
                hasProperty("hensikt", equalTo("hen2"))
        )));

        assertThat(testgruppe.getFavorisertAv().isEmpty(), is(true));
    }

    @Test
    public void leggTilTeam() {

        Bruker bruker = new Bruker();

        brukerService.leggTilTeam(bruker, new Team());

        verify(brukerRepository).save(bruker);
    }

    @Test
    public void fetchBrukere() {

        brukerService.fetchBrukere();

        verify(brukerRepository).findAllByOrderByNavIdent();
    }

    @Test
    public void sletteBrukerFavoritterByTeamId() {

        long teamId = 1L;

        brukerService.sletteBrukerFavoritterByTeamId(teamId);

        verify(brukerRepository).deleteBrukerFavoritterByTeamId(teamId);
    }

    @Test
    public void sletteBrukerFavoritterByGroupId() {

        long groupId = 1L;

        brukerService.sletteBrukerFavoritterByGroupId(groupId);

        verify(brukerRepository).deleteBrukerFavoritterByGroupId(groupId);
    }
}