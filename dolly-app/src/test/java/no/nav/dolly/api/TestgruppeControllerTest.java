package no.nav.dolly.api;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.BestKriterier;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeControllerTest {

    private static final String IDENT = "12345678901";
    private static final Long GRUPPE_ID = 1L;
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private TestgruppeService testgruppeService;

    @Mock
    private IdentService identService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private DollyBestillingService dollyBestillingService;

    @Mock
    private BestillingService bestillingService;

    @InjectMocks
    private TestgruppeController controller;

    @Test
    public void opprettTestgruppe() {
        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = Testgruppe.builder().id(1L).build();
        when(testgruppeService.opprettTestgruppe(gruppe)).thenReturn(testgruppe);

        controller.opprettTestgruppe(gruppe);
        verify(testgruppeService).fetchTestgruppeById(1L);
    }

    @Test
    public void oppdaterTestgruppe() {

        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = new Testgruppe();
        when(testgruppeService.oppdaterTestgruppe(GRUPPE_ID, gruppe)).thenReturn(testgruppe);

        controller.oppdaterTestgruppe(GRUPPE_ID, gruppe);

        verify(testgruppeService).oppdaterTestgruppe(GRUPPE_ID, gruppe);
    }

    @Test
    public void deleteTestidenter() {
        List<RsTestident> testpersonIdentListe = singletonList(new RsTestident());
        controller.deleteTestident(testpersonIdentListe);
        verify(identService).slettTestidenter(testpersonIdentListe);
    }

    @Test(expected = NotFoundException.class)
    public void deleteTestIdentNotFound() {
        controller.deleteTestident(IDENT);
    }

    @Test
    public void deleteTestIdentFound() {
        when(identService.slettTestident(IDENT)).thenReturn(1);
        controller.deleteTestident(IDENT);
        verify(identService).slettTestident(IDENT);
    }

    @Test
    public void getTestgruppe() {
        RsTestgruppeUtvidet testgruppeUtvidet = new RsTestgruppeUtvidet();
        testgruppeUtvidet.setId(GRUPPE_ID);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(new Testgruppe());
        when(mapperFacade.map(any(Testgruppe.class), eq(RsTestgruppeUtvidet.class))).thenReturn(testgruppeUtvidet);

        RsTestgruppeUtvidet result = controller.getTestgruppe(GRUPPE_ID);

        assertThat(result.getId(), is(equalTo(GRUPPE_ID)));

        verify(testgruppeService).fetchTestgruppeById(GRUPPE_ID);
        verify(mapperFacade).map(any(Testgruppe.class), eq(RsTestgruppeUtvidet.class));
    }

    @Test
    public void getTestgrupper() {
        controller.getTestgrupper("nav", GRUPPE_ID);
        verify(testgruppeService).getTestgruppeByNavidentOgTeamId("nav", GRUPPE_ID);
    }

    @Test
    public void oppretteIdentBestilling() {
        int ant = 1;
        List<String> envir = singletonList("u");

        RsDollyBestillingRequest dollyBestillingRequest = new RsDollyBestillingRequest();
        dollyBestillingRequest.setAntall(ant);

        dollyBestillingRequest.setEnvironments(envir);

        Bestilling bestilling = Bestilling.builder().id(BESTILLING_ID).build();

        when(bestillingService.saveBestilling(eq(GRUPPE_ID), eq(ant), anyList(), any(), any(BestKriterier.class), any())).thenReturn(bestilling);

        controller.opprettIdentBestilling(GRUPPE_ID, dollyBestillingRequest);
        verify(dollyBestillingService).opprettPersonerByKriterierAsync(GRUPPE_ID, dollyBestillingRequest, bestilling);
    }

    @Test(expected = NotFoundException.class)
    public void slettGruppeIdNotFound() {
        controller.slettgruppe(anyLong());
        verify(testgruppeService).slettGruppeById(anyLong());
    }

    @Test
    public void slettGruppeIdFound() {
        when(testgruppeService.slettGruppeById(anyLong())).thenReturn(1);
        controller.slettgruppe(anyLong());
        verify(testgruppeService).slettGruppeById(anyLong());
    }

    @Test
    public void getIdentsByGroupId_hentIdenter() {
        controller.getIdentsByGroupId(anyLong());
        verify(testgruppeService).fetchIdenterByGruppeId(anyLong());
    }

    @Test
    public void oppretteIdentBestillingFraEksisterende() {
        List<String> envir = singletonList("u");

        RsDollyBestillingFraIdenterRequest dollyBestillingsRequest = new RsDollyBestillingFraIdenterRequest();
        dollyBestillingsRequest.getOpprettFraIdenter().add(IDENT);
        dollyBestillingsRequest.setEnvironments(envir);

        Bestilling bestilling = Bestilling.builder().id(BESTILLING_ID).build();

        when(bestillingService.saveBestilling(eq(GRUPPE_ID), eq(1), anyList(), any(), any(BestKriterier.class), anyList())).thenReturn(bestilling);

        controller.opprettIdentBestillingFraIdenter(GRUPPE_ID, dollyBestillingsRequest);
        verify(dollyBestillingService).opprettPersonerFraIdenterMedKriterierAsync(GRUPPE_ID, dollyBestillingsRequest, bestilling);
    }
}