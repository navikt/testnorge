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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeServiceTest {

    private static final long GROUP_ID = 1L;
    private static final String IDENT_ONE = "1";
    private static final String IDENT_TWO = "2";
    private static final String standardPrincipal = "PRINC";

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private IdentService identService;

    @Mock
    private PersonService personService;

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
        Bruker bruker = mock(Bruker.class);

        when(brukerService.fetchBruker(standardPrincipal)).thenReturn(bruker);

        testgruppeService.opprettTestgruppe(rsTestgruppe);

        ArgumentCaptor<Testgruppe> cap = ArgumentCaptor.forClass(Testgruppe.class);
        verify(testgruppeRepository).save(cap.capture());

        Testgruppe res = cap.getValue();

        assertThat(res.getOpprettetAv(), is(bruker));
        assertThat(res.getSistEndretAv(), is(bruker));
    }

    @Test(expected = NotFoundException.class)
    public void fetchTestgruppeById_KasterExceptionHvisGruppeIkkeErFunnet() throws Exception {
        Optional<Testgruppe> op = Optional.empty();
        when(testgruppeRepository.findById(any())).thenReturn(op);

        testgruppeService.fetchTestgruppeById(1L);
    }

    @Test
    public void fetchTestgruppeById_ReturnererGruppeHvisGruppeMedIdFinnes() throws Exception {
        Testgruppe g = mock(Testgruppe.class);
        Optional<Testgruppe> op = Optional.of(g);
        when(testgruppeRepository.findById(any())).thenReturn(op);

        Testgruppe hentetGruppe = testgruppeService.fetchTestgruppeById(1L);

        assertThat(g, is(hentetGruppe));
    }

    @Test
    public void fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker() {
        Testgruppe tg1 = Testgruppe.builder().id(1L).navn("test1").build();
        Testgruppe tg2 = Testgruppe.builder().id(2L).navn("test2").build();
        Testgruppe tg3 = Testgruppe.builder().id(3L).navn("test3").build();

        Bruker bruker = Bruker.builder()
                .favoritter(newHashSet(asList(tg1, tg2, tg3)))
                .brukerId(standardPrincipal)
                .build();

        when(brukerService.fetchBruker(any())).thenReturn(bruker);

        List<Testgruppe> grupper = testgruppeService.fetchTestgrupperByBrukerId(standardPrincipal);

        assertThat(grupper, hasItem(hasProperty("id", equalTo(1L))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(2L))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(3L))));
    }

    @Test
    public void saveGruppeTilDB_returnererTestgruppeHvisTestgruppeFinnes() {
        Testgruppe g = new Testgruppe();
        when(testgruppeRepository.save(any())).thenReturn(g);

        Testgruppe res = testgruppeService.saveGruppeTilDB(new Testgruppe());
        assertThat(res, is(notNullValue()));
    }

    @Test
    public void slettGruppeById_deleteBlirKaltMotRepoMedGittId() {
        testgruppeService.slettGruppeById(GROUP_ID);
        verify(brukerService).sletteBrukerFavoritterByGroupId(GROUP_ID);
        verify(testgruppeRepository).deleteTestgruppeById(GROUP_ID);
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveGruppeTilDB_kasterExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        testgruppeService.saveGruppeTilDB(new Testgruppe());
    }

    @Test(expected = DollyFunctionalException.class)
    public void saveGruppeTilDB_kasterDollyExceptionHvisDBConstraintErBrutt() throws Exception {
        when(testgruppeRepository.save(any())).thenThrow(nonTransientDataAccessException);
        testgruppeService.saveGruppeTilDB(new Testgruppe());
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveGrupper_kasterExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.saveAll(any())).thenThrow(DataIntegrityViolationException.class);
        testgruppeService.saveGrupper(newHashSet(singletonList(new Testgruppe())));
    }

    @Test(expected = DollyFunctionalException.class)
    public void saveGrupper_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.saveAll(any())).thenThrow(nonTransientDataAccessException);
        testgruppeService.saveGrupper(newHashSet(singletonList(new Testgruppe())));
    }

    @Test(expected = NotFoundException.class)
    public void fetchGrupperByIdsIn_kasterExceptionOmGruppeIkkeFinnes() {
        testgruppeService.fetchGrupperByIdsIn(singletonList(anyLong()));
    }

    @Test
    public void oppdaterTestgruppe_sjekkAtDBKalles() {
        long teamId = 2L;

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder().hensikt("test").navn("navn").teamId(1L).build();

        when(testgruppeRepository.findById(anyLong())).thenReturn(Optional.of(testGruppe));
        when(brukerService.fetchBruker(anyString())).thenReturn( Bruker.builder().brukerId("brukerId").build());
        testgruppeService.oppdaterTestgruppe(GROUP_ID, rsOpprettEndreTestgruppe);
        verify(testgruppeRepository).save(testGruppe);
    }

    @Test
    public void getTestgrupper() {
        testgruppeService.getTestgruppeByBrukerId(null);
        verify(testgruppeRepository).findAllByOrderByNavn();
    }
}