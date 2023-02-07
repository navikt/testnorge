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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestgruppeControllerTest {

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
    void opprettTestgruppe() {
        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = Testgruppe.builder().id(1L).build();
        when(testgruppeService.opprettTestgruppe(gruppe)).thenReturn(testgruppe);

        testgruppeController.opprettTestgruppe(gruppe);
        verify(testgruppeService).fetchTestgruppeById(1L);
    }

    @Test
    void oppdaterTestgruppe() {

        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = new Testgruppe();
        when(testgruppeService.oppdaterTestgruppe(GRUPPE_ID, gruppe)).thenReturn(testgruppe);

        testgruppeController.oppdaterTestgruppe(GRUPPE_ID, gruppe);

        verify(testgruppeService).oppdaterTestgruppe(GRUPPE_ID, gruppe);
    }

    @Test
    void getTestgrupper() {
        testgruppeController.getTestgrupper(0, 10, "nav");
        verify(testgruppeService).getTestgruppeByBrukerId(0, 10, "nav");
    }

    @Test
    void oppretteIdentBestilling() {
        int ant = 1;
        Set<String> envir = singleton("u");

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