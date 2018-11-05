package no.nav.dolly.api;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.appservices.tpsf.service.DollyTpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsOpprettTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeMedErMedlemOgFavoritt;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeControllerTest {

    @Mock
    private TestgruppeService testgruppeService;

    @Mock
    private IdentService identService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private DollyTpsfService dollyTpsfService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private BestillingProgressService bestillingProgressService;

    @InjectMocks
    private TestgruppeController controller;

    @Test
    public void opprettTestgruppe() {
        RsOpprettTestgruppe gruppe = new RsOpprettTestgruppe();
        RsTestgruppe g = new RsTestgruppe();
        when(testgruppeService.opprettTestgruppe(gruppe)).thenReturn(g);

        controller.opprettTestgruppe(gruppe);
        verify(testgruppeService).rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(g);
    }

    @Test
    public void oppdaterTestgruppe() {
        Long gId = 1l;
        RsOpprettTestgruppe gruppe = new RsOpprettTestgruppe();
        RsTestgruppe g = new RsTestgruppe();
        when(testgruppeService.oppdaterTestgruppe(gId, gruppe)).thenReturn(g);

        controller.oppdaterTestgruppe(gId, gruppe);
        verify(testgruppeService).rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(g);
    }

    @Test
    public void deleteTestident() {
        List<RsTestident> testpersonIdentListe = Arrays.asList(new RsTestident());
        controller.deleteTestident(testpersonIdentListe);
        verify(identService).slettTestidenter(testpersonIdentListe);
    }

    @Test
    public void getTestgruppe() {
        Long gId = 1l;
        RsTestgruppe g = new RsTestgruppe();
        RsTestgruppeMedErMedlemOgFavoritt gMedMedlemFavo = new RsTestgruppeMedErMedlemOgFavoritt();
        Testgruppe testgruppe = new Testgruppe();
        Bestilling bestilling = new Bestilling();
        RsBestilling rsBestilling = new RsBestilling();
        List<Bestilling> bestillinger = Arrays.asList(bestilling);
        List<RsBestilling> rsBestillinger = Arrays.asList(rsBestilling);

        when(testgruppeService.fetchTestgruppeById(gId)).thenReturn(testgruppe);
        when(mapperFacade.map(testgruppe, RsTestgruppe.class)).thenReturn(g);
        when(testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(g)).thenReturn(gMedMedlemFavo);
        when(bestillingService.fetchBestillingerByGruppeId(gId)).thenReturn(bestillinger);
        when(mapperFacade.mapAsList(bestillinger, RsBestilling.class)).thenReturn(rsBestillinger);

        RsTestgruppeMedErMedlemOgFavoritt res = controller.getTestgruppe(gId);

        assertThat(res.getBestillinger(), is(rsBestillinger));
    }

    @Test
    public void getTestgrupper() {
        controller.getTestgrupper("nav", 1l);
        verify(testgruppeService).getTestgruppeByNavidentOgTeamId("nav", 1l);
    }

    @Test
    public void oppretteIdentBestilling() {
        Long gId = 1l;

        RsDollyBestillingsRequest bes = new RsDollyBestillingsRequest();
        List<String> envir = Arrays.asList("u");
        int ant = 1;
        bes.setEnvironments(envir);
        bes.setAntall(ant);

        Bestilling b = new Bestilling();
        b.setId(2l);

        when(bestillingService.saveBestillingByGruppeIdAndAntallIdenter(gId, ant, envir)).thenReturn(b);
        controller.oppretteIdentBestilling(gId, bes);
        verify(dollyTpsfService).opprettPersonerByKriterierAsync(gId, bes, 2l);
    }

    @Test
    public void slettgruppe_metodekall(){
        controller.slettgruppe(anyLong());
        verify(testgruppeService).slettGruppeById(anyLong());
    }

    @Test
    public void getIdentsByGroupId_hentIdenter() {
        controller.getIdentsByGroupId(anyLong());
        verify(testgruppeService).fetchIdenterByGruppeId(anyLong());
    }
}