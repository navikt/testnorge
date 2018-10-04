package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DollyTpsfServiceTest {

    private static final String SUCCESS_CODE_TPS = "00";
    private static final String FAIL_CODE_TPS = "08";
    private static final String FEILMELDING_OPPRETTING_FEILET = "##### LAGE IDENTER FEILET ######  Error";

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;
    private Map<String, String> status_SuccU1T2_FailQ3 = new HashMap<>();
    private RsDollyBestillingsRequest standardBestillingRequest_u1_t2_q3 = new RsDollyBestillingsRequest();
    private RsTpsfBestilling tpsfReqEmpty = new RsTpsfBestilling();
    private TpsfException standardTpsfException = new TpsfException("feil");
    private Bestilling standardNyBestilling = new Bestilling();
    private Testgruppe standardGruppe = new Testgruppe();
    private SendSkdMeldingTilTpsResponse standardSkdResponse = new SendSkdMeldingTilTpsResponse();
    private Long standardGruppeId = 1L;
    private Long bestillingsId = 2l;
    private String standardHovedident = "10";
    private String standardFeilmelding = "feil";
    List<String> standardIdenter = Arrays.asList(standardHovedident, "34", "56");

    @Mock
    IdentRepository identRepository;

    @Mock
    BestillingProgressRepository bestillingProgressRepository;

    @Mock
    TpsfApiService tpsfApiService;

    @Mock
    IdentService identService;

    @Mock
    TestgruppeService testgruppeService;

    @Mock
    SigrunStubApiService sigrunStubApiService;

    @Mock
    BestillingService bestillingService;

    @InjectMocks
    DollyTpsfService dollyTpsfService;

    @Before
    public void setup(){
        System.setOut(new PrintStream(outContent));

        standardSkdResponse.setPersonId(standardHovedident);

        standardNyBestilling.setId(bestillingsId);
        standardNyBestilling.setFerdig(false);

        status_SuccU1T2_FailQ3.put("u1", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("t2", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("q3", FAIL_CODE_TPS);

        standardBestillingRequest_u1_t2_q3.setEnvironments(Arrays.asList("u1", "t2", "q3"));
        standardBestillingRequest_u1_t2_q3.setAntall(1);
        standardBestillingRequest_u1_t2_q3.setTpsf(tpsfReqEmpty);
    }

    @After
    public void after(){
        System.setOut(originalOut);
    }

    @Test
    public void opprettPersonerByKriterierAsync_bestillingBlirSattFerdigNaarExceptionKastesUnderOppretting() {
        when(tpsfApiService.opprettIdenterTpsf(standardBestillingRequest_u1_t2_q3.getTpsf())).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);
        when(bestillingProgressRepository.save(any())).thenThrow(standardTpsfException);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        String out = outContent.toString().substring(0, outContent.toString().indexOf(':'));

        assertThat(out, is(FEILMELDING_OPPRETTING_FEILET));
        assertThat(standardNyBestilling.isFerdig(), is(true));
        verify(bestillingService).saveBestillingToDB(standardNyBestilling);
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerAlleMiljoeneSomErsuksessfulleSendtTilTPSTilBestilllingProgress(){
        standardSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse response = new RsSkdMeldingResponse();
        response.setSendSkdMeldingTilTpsResponsene(Arrays.asList(standardSkdResponse));

        when(tpsfApiService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(response);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(bestillingProgressRepository).save(argumentCaptor.capture());

        BestillingProgress bestillingProgress = argumentCaptor.getValue();

        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("u1"), is(true));
        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("t2"), is(true));
        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("q3"), is(true));
        assertThat(bestillingProgress.getFeil(), is(nullValue()));
    }

    @Test
    public void opprettPersonerByKriterierAsync_hvisFlereIdenterBestillesOgEnFeilerOgEnOkSaaEtBestillingProgressObjMedFeilOgEtMedOk(){
        int bestiltAntallIdenter = 2;
        standardBestillingRequest_u1_t2_q3.setAntall(bestiltAntallIdenter);
        standardSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse response = new RsSkdMeldingResponse();
        response.setSendSkdMeldingTilTpsResponsene(Arrays.asList(standardSkdResponse));

        TpsfException tpsfException = new TpsfException(standardFeilmelding);

        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(response).thenThrow(tpsfException);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService, times(1)).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(bestillingProgressRepository, times(2)).save(argumentCaptor.capture());

        List<BestillingProgress> bestillingProgresses = argumentCaptor.getAllValues();
        BestillingProgress bestillingProgressOK = bestillingProgresses.get(0);
        BestillingProgress bestillingProgressFEIL = bestillingProgresses.get(1);

        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("u1"), is(true));
        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("t2"), is(true));
        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("q3"), is(true));
        assertThat(bestillingProgressOK.getFeil(), is(nullValue()));

        assertThat(bestillingProgressFEIL.getTpsfSuccessEnv(), is(nullValue()));
        assertThat(bestillingProgressFEIL.getFeil(), is(notNullValue()));
        assertThat(bestillingProgressFEIL.getFeil().contains(standardFeilmelding), is(true));
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerFeilIProgressHvisSendingAvIdenterTilTpsMiljoFeiler() throws Exception {
        standardSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse response = new RsSkdMeldingResponse();
        response.setSendSkdMeldingTilTpsResponsene(Arrays.asList(standardSkdResponse));
        TpsfException tpsfException = new TpsfException(standardFeilmelding);

        when(tpsfApiService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(tpsfException);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService, never()).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(bestillingProgressRepository).save(argumentCaptor.capture());

        BestillingProgress bestillingProgress = argumentCaptor.getValue();

        assertThat(bestillingProgress.getFeil(), is(notNullValue()));
        assertThat(bestillingProgress.getFeil().contains(standardFeilmelding), is(true));
    }


}