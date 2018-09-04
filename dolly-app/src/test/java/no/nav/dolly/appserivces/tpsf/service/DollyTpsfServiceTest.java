package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DollyTpsfServiceTest {

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;


    @Mock
    TpsfApiService tpsfApiService;

    @Mock
    TestgruppeService testgruppeService;

//    @Mock
//    SigrunStubApiService sigrunStubApiService;
//
//    @Mock
//    IdentRepository identRepository;

    @Mock
    BestillingProgressRepository bestillingProgressRepository;

    @Mock
    BestillingService bestillingService;

    @InjectMocks
    DollyTpsfService dollyTpsfService;

    @Before
    public void setup(){
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void after(){
        System.setOut(originalOut);
    }

    @Test
    public void opprettPersonerByKriterierAsync_bestillingBlirSattFerdigNaarExceptionKastes() throws Exception {
        Long gruppeId = 1L;
        Long bestillingsId = 2L;
        RsDollyBestillingsRequest request = new RsDollyBestillingsRequest();
        request.setEnvironments(Arrays.asList("u1", "t2"));

        List<String> identer = Arrays.asList("12", "34", "56");

        Testgruppe gruppe = new Testgruppe();
        Bestilling bestilling = new Bestilling();
        bestilling.setId(bestillingsId);
        bestilling.setFerdig(false);
        TpsfException exception = new TpsfException("feil");

        when(tpsfApiService.opprettPersonerTpsf(request)).thenReturn(identer);
        when(testgruppeService.fetchTestgruppeById(gruppeId)).thenReturn(gruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(bestilling);
        when(tpsfApiService.sendTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);
        when(bestillingProgressRepository.save(any())).thenThrow(exception);

        dollyTpsfService.opprettPersonerByKriterierAsync(gruppeId, request, bestillingsId);

        String out = outContent.toString().substring(0, outContent.toString().indexOf('!'));
        assertThat(out, is("Lage identer feiler. Dette bor logges"));
        assertThat(bestilling.isFerdig(), is(true));

        verify(bestillingService).saveBestillingToDB(bestilling);
    }

    @Test
    public void opprettPersonerByKriterierAsync_noe(){

    }
}