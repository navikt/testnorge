package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DollyTpsfServiceTest {

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;


    @Mock
    TpsfApiService tpsfApiService;

    @Mock
    TestgruppeService testgruppeService;

    @Mock
    SigrunStubApiService sigrunStubApiService;

    @Mock
    IdentRepository identRepository;

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
//        Long gruppeId = 1L;
//        Long bestillingsId = 2L;
//        RsDollyBestillingsRequest request = new RsDollyBestillingsRequest();
//        request.setEnvironments(Arrays.asList("u1", "t2"));
//
//        List<String> identer = Arrays.asList("12", "34", "56");
//
//        Testgruppe gruppe = new Testgruppe();
//        Bestilling bestilling = new Bestilling();
//        bestilling.setId(bestillingsId);
//        bestilling.setFerdig(false);
//        TpsfException exception = new TpsfException("feil");
//
//        when(tpsfApiService.opprettPersonerTpsf(request)).thenReturn(identer);
//        when(testgruppeService.fetchTestgruppeById(gruppeId)).thenReturn(gruppe);
//        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(bestilling);
//        when(tpsfApiService.sendTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);
//        when(bestillingProgressRepository.save(any())).thenThrow(exception);
//
//        dollyTpsfService.opprettPersonerByKriterierAsync(gruppeId, request, bestillingsId);
//
//        String out = outContent.toString().substring(0, outContent.toString().indexOf('!'));
//        assertThat(out, is("Lage identer feiler. Dette bor logges"));
//        assertThat(bestilling.isFerdig(), is(true));
//
//        verify(bestillingService).saveBestillingToDB(bestilling);
    }

    @Test
    public void opprettPersonerByKriterierAsync_happyPathMedKunTpsfData(){
//        Long gruppeId = 1L;
//        Long bestillingsId = 2L;
//        RsDollyBestillingsRequest request = new RsDollyBestillingsRequest();
//        request.setEnvironments(Arrays.asList("u1", "t2"));
//
//        String i1= "12";
//        String i2 = "34";
//        String i3 = "56";
//        List<String> identer = Arrays.asList(i1, i2,i3);
//
//        Testgruppe gruppe = new Testgruppe();
//        Bestilling bestilling = new Bestilling();
//        bestilling.setId(bestillingsId);
//        bestilling.setFerdig(false);
//        TpsfException exception = new TpsfException("feil");
//
//        SendSkdMeldingTilTpsResponse sendSkdResponse = new SendSkdMeldingTilTpsResponse();
//        Map<String, String> status = new HashMap<>();
//        status.put("u6", "00");
//        sendSkdResponse.setStatus(status);
//
//        RsSkdMeldingResponse skdResponse = new RsSkdMeldingResponse();
//        skdResponse.setGruppeid(gruppeId);
//        skdResponse.setSendSkdMeldingTilTpsResponsene(Arrays.asList(sendSkdResponse));
//
//        BestillingProgress progress = new BestillingProgress();
//        Testident ident = new Testident();
//
//        when(tpsfApiService.opprettPersonerTpsf(request)).thenReturn(identer);
//        when(testgruppeService.fetchTestgruppeById(gruppeId)).thenReturn(gruppe);
//        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(bestilling);
//        when(tpsfApiService.sendTilTpsFraTPSF(any(), any())).thenReturn(skdResponse);
//        when(bestillingProgressRepository.save(any())).thenReturn(progress);
//        when(bestillingService.saveBestillingToDB(any(Bestilling.class))).thenReturn(new Bestilling());
//        when(identRepository.save(any())).thenReturn(ident);
//
//        dollyTpsfService.opprettPersonerByKriterierAsync(gruppeId, request, bestillingsId);
//
//        ArgumentCaptor<Testident> identCaptor = ArgumentCaptor.forClass(Testident.class);
//        verify(identRepository, times(3)).save(identCaptor.capture());
//        verify(sigrunStubApiService, never()).createInntektstuff(any());
//
//        List<Testident> s = identCaptor.getAllValues();
//
//        assertThat(s.size(), is(3));
//
//        assertThat(s, hasItem(both(
//                hasProperty("ident", equalTo(i1))).and(
//                hasProperty("testgruppe", is(gruppe)))
//        ));
//
//        assertThat(s, hasItem(both(
//                hasProperty("ident", equalTo(i2))).and(
//                hasProperty("testgruppe", is(gruppe)))
//        ));
//
//        assertThat(s, hasItem(both(
//                hasProperty("ident", equalTo(i3))).and(
//                hasProperty("testgruppe", is(gruppe)))
//        ));
    }
}