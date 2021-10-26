package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.OpprettPersonerByKriterierService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.provider.api.TestgruppeController;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeControllerTest {

    private static final Long GRUPPE_ID = 1L;
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private TestgruppeService testgruppeService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private OpprettPersonerByKriterierService opprettPersonerByKriterierService;

    @InjectMocks
    private TestgruppeController testgruppeController;

    @Test
    public void opprettTestgruppe() {
        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = Testgruppe.builder().id(1L).build();
        when(testgruppeService.opprettTestgruppe(gruppe)).thenReturn(testgruppe);

        testgruppeController.opprettTestgruppe(gruppe);
        verify(testgruppeService).fetchTestgruppeById(1L);
    }

    @Test
    public void oppdaterTestgruppe() {

        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = new Testgruppe();
        when(testgruppeService.oppdaterTestgruppe(GRUPPE_ID, gruppe)).thenReturn(testgruppe);

        testgruppeController.oppdaterTestgruppe(GRUPPE_ID, gruppe);

        verify(testgruppeService).oppdaterTestgruppe(GRUPPE_ID, gruppe);
    }

    @Test
    public void getTestgrupper() {
        testgruppeController.getTestgrupper("nav");
        verify(testgruppeService).getTestgruppeByBrukerId("nav");
    }

    @Test
    public void oppretteIdentBestilling() {
        int ant = 1;
        List<String> envir = singletonList("u");

        RsDollyBestillingRequest dollyBestillingRequest = new RsDollyBestillingRequest();
        dollyBestillingRequest.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        dollyBestillingRequest.setAntall(ant);
        dollyBestillingRequest.setEnvironments(envir);

        Bestilling bestilling = Bestilling.builder().id(BESTILLING_ID).build();

        when(bestillingService.saveBestilling(eq(GRUPPE_ID), any(RsDollyBestilling.class), any(RsTpsfUtvidetBestilling.class), eq(ant), eq(null), eq(null), eq(null))).thenReturn(bestilling);

        testgruppeController.opprettIdentBestilling(GRUPPE_ID, dollyBestillingRequest);
        verify(opprettPersonerByKriterierService).executeAsync(bestilling);
    }
}