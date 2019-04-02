package no.nav.dolly.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;

@RunWith(MockitoJUnitRunner.class)
public class BestillingServiceTest {

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private TestgruppeService testgruppeService;

    @InjectMocks
    private BestillingService bestillingService;

    @Test(expected = NotFoundException.class)
    public void fetchBestillingByIdKasterExceptionHvisBestillingIkkeFunnet() throws Exception {
        Optional<Bestilling> bes = Optional.empty();

        when(bestillingRepository.findById(any())).thenReturn(bes);

        bestillingService.fetchBestillingById(1l);
    }

    @Test
    public void fetchBestillingByIdKasterReturnererBestillingHvisBestillingErFunnet() throws Exception {
        Bestilling mock = Mockito.mock(Bestilling.class);
        Optional<Bestilling> bes = Optional.of(mock);

        when(bestillingRepository.findById(any())).thenReturn(bes);

        Bestilling bestilling = bestillingService.fetchBestillingById(1l);

        assertThat(bestilling, is(mock));
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveBestillingToDBKasterExceptionHvisDBConstraintBlirBrutt() throws Exception {
        when(bestillingRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        bestillingService.saveBestillingToDB(new Bestilling());
    }

    @Test
    public void fetchBestillingerByGruppeIdBlirKaltMedGittFunnetTestgruppeOgReturnererBestillinger() {
        Testgruppe gruppe = Mockito.mock(Testgruppe.class);
        when(testgruppeService.fetchTestgruppeById(any())).thenReturn(gruppe);

        bestillingService.fetchBestillingerByGruppeId(1l);
        verify(bestillingRepository).findBestillingByGruppeOrderById(gruppe);
    }

    @Test
    public void saveBestillingByGruppeIdAndAntallIdenterInkludererAlleMiljoerOgIdenterIBestilling() {
        long gruppeId = 1l;
        Testgruppe gruppe = Mockito.mock(Testgruppe.class);
        List<String> miljoer = Arrays.asList("a1", "b2", "c3", "d4");
        int antallIdenter = 4;

        when(testgruppeService.fetchTestgruppeById(gruppeId)).thenReturn(gruppe);

        bestillingService.saveBestilling(gruppeId, antallIdenter, miljoer, null, null, null);

        ArgumentCaptor<Bestilling> argCap = ArgumentCaptor.forClass(Bestilling.class);
        verify(bestillingRepository).save(argCap.capture());

        Bestilling bes = argCap.getValue();

        assertThat(bes.getGruppe(), is(gruppe));
        assertThat(bes.getAntallIdenter(), is(antallIdenter));
        assertThat(bes.getMiljoer(), is("a1,b2,c3,d4"));
    }

}