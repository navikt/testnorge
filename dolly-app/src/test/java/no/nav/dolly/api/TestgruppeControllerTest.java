package no.nav.dolly.api;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
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
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
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
        Long gId = 1L;
        RsOpprettEndreTestgruppe gruppe = new RsOpprettEndreTestgruppe();
        Testgruppe testgruppe = new Testgruppe();
        when(testgruppeService.oppdaterTestgruppe(gId, gruppe)).thenReturn(testgruppe);

        controller.oppdaterTestgruppe(gId, gruppe);

        verify(testgruppeService).oppdaterTestgruppe(gId, gruppe);
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
        Long gruppeId = 1L;
        RsTestgruppeUtvidet testgruppeUtvidet = new RsTestgruppeUtvidet();
        Testgruppe testgruppe = new Testgruppe();

        when(testgruppeService.fetchTestgruppeById(gruppeId)).thenReturn(testgruppe);
        when(mapperFacade.map(testgruppe, RsTestgruppeUtvidet.class)).thenReturn(testgruppeUtvidet);

        RsTestgruppeUtvidet result = controller.getTestgruppe(gruppeId);

        assertThat(result.getBestillinger(), is(emptyList()));
    }

    @Test
    public void getTestgrupper() {
        controller.getTestgrupper("nav", 1L);
        verify(testgruppeService).getTestgruppeByNavidentOgTeamId("nav", 1L);
    }

    @Test
    public void oppretteIdentBestilling() {
        Long gruppeId = 1L;
        Long bestillingId = 2L;
        int ant = 1;
        List<String> envir = singletonList("u");

        RsDollyBestillingsRequest dollyBestillingsRequest = RsDollyBestillingsRequest.builder()
                .environments(envir)
                .antall(ant)
                .build();

        Bestilling bestilling = Bestilling.builder().id(bestillingId).build();

        when(bestillingService.saveBestillingByGruppeIdAndAntallIdenter(gruppeId, ant, envir)).thenReturn(bestilling);

        controller.opprettIdentBestilling(gruppeId, dollyBestillingsRequest);
        verify(dollyBestillingService).opprettPersonerByKriterierAsync(gruppeId, dollyBestillingsRequest, bestillingId);
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
}