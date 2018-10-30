package no.nav.dolly.appservices.tpsf.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.appservices.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appservices.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@RunWith(MockitoJUnitRunner.class)
public class DollyTpsfServiceTest {

    private static final String SUCCESS_CODE_TPS = "00";
    private static final String FAIL_CODE_TPS = "08";
    private static final String INNVANDRING_CREATE_NAVN = "InnvandringCreate";

    private Map<String, String> status_SuccU1T2_FailQ3;
    private RsDollyBestillingsRequest standardBestillingRequest_u1_t2_q3;
    private RsTpsfBestilling tpsfReqEmpty = new RsTpsfBestilling();
    private TpsfException standardTpsfException = new TpsfException("feil");
    private Bestilling standardNyBestilling;
    private Testgruppe standardGruppe = new Testgruppe();
    private SendSkdMeldingTilTpsResponse standardSendSkdResponse;
    private Long standardGruppeId = 1L;
    private Long bestillingsId = 2l;
    private String standardHovedident = "10";
    private String standardFeilmelding = "feil";
    private String standardTpsFeedback = "feedback";
    private List<String> standardIdenter = Arrays.asList(standardHovedident, "34", "56");

    @Mock
    private IdentRepository identRepository;

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private TpsfApiService tpsfApiService;

    @Mock
    private IdentService identService;

    @Mock
    private TestgruppeService testgruppeService;

    @Mock
    private SigrunStubApiService sigrunStubApiService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private TpsfResponseHandler tpsfResponseHandler;

    @InjectMocks
    private DollyTpsfService dollyTpsfService;

    @Mock
    private List<RsSigrunnOpprettSkattegrunnlag> rsSigrunnOpprettSkattegrunnlag;

    @Mock
    private SigrunResponseHandler sigrunResponseHandler;

    @Before
    public void setup() {
        standardSendSkdResponse = new SendSkdMeldingTilTpsResponse();
        standardSendSkdResponse.setPersonId(standardHovedident);
        standardSendSkdResponse.setSkdmeldingstype(INNVANDRING_CREATE_NAVN);

        standardNyBestilling = new Bestilling();
        standardNyBestilling.setId(bestillingsId);
        standardNyBestilling.setFerdig(false);

        status_SuccU1T2_FailQ3 = new HashMap<>();
        status_SuccU1T2_FailQ3.put("u1", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("t2", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("q3", FAIL_CODE_TPS);

        standardBestillingRequest_u1_t2_q3 = new RsDollyBestillingsRequest();
        standardBestillingRequest_u1_t2_q3.setEnvironments(Arrays.asList("u1", "t2", "q3"));
        standardBestillingRequest_u1_t2_q3.setAntall(1);
        standardBestillingRequest_u1_t2_q3.setTpsf(tpsfReqEmpty);
        standardBestillingRequest_u1_t2_q3.setSigrunRequest(rsSigrunnOpprettSkattegrunnlag);
    }

    @Test
    public void opprettPersonerByKriterierAsync_bestillingBlirSattFerdigNaarExceptionKastesUnderOppretting() {
        when(tpsfApiService.opprettIdenterTpsf(standardBestillingRequest_u1_t2_q3.getTpsf())).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);
        when(bestillingProgressRepository.save(any())).thenThrow(standardTpsfException);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        assertThat(standardNyBestilling.isFerdig(), is(true));
        verify(bestillingService).saveBestillingToDB(standardNyBestilling);
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerAlleMiljoeneSomErsuksessfulleSendtTilTPSTilBestilllingProgress() {
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse skdMeldingResponse = new RsSkdMeldingResponse();
        skdMeldingResponse.setSendSkdMeldingTilTpsResponsene(Arrays.asList(standardSendSkdResponse));

        when(tpsfApiService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(skdMeldingResponse);
        when(tpsfResponseHandler.extractTPSFeedback(anyList())).thenReturn(standardTpsFeedback);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(bestillingProgressRepository, times(2)).save(argumentCaptor.capture());

        BestillingProgress bestillingProgress = argumentCaptor.getValue();

        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("u1"), is(true));
        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("t2"), is(true));
        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("q3"), is(false));
        assertThat(bestillingProgress.getFeil(), is(nullValue()));
    }

    @Test
    public void opprettPersonerByKriterierAsync_hvisFlereIdenterBestillesOgEnFeilerOgEnOkSaaEtBestillingProgressObjMedFeilOgEtMedOk() {
        int bestiltAntallIdenter = 2;
        standardBestillingRequest_u1_t2_q3.setAntall(bestiltAntallIdenter);
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse response = new RsSkdMeldingResponse();
        response.setSendSkdMeldingTilTpsResponsene(Arrays.asList(standardSendSkdResponse));

        TpsfException tpsfException = new TpsfException(standardFeilmelding);

        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(response).thenThrow(tpsfException);
        when(tpsfResponseHandler.extractTPSFeedback(anyList())).thenReturn(standardTpsFeedback);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService, times(1)).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(bestillingProgressRepository, times(4)).save(argumentCaptor.capture());
        verify(tpsfResponseHandler).setErrorMessageToBestillingsProgress(any(TpsfException.class), any(BestillingProgress.class));

        List<BestillingProgress> bestillingProgresses = argumentCaptor.getAllValues();
        BestillingProgress bestillingProgressOK = bestillingProgresses.get(0);

        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("u1"), is(true));
        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("t2"), is(true));
        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("q3"), is(false));
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerFeilIProgressHvisSendingAvIdenterTilTpsMiljoFeiler() throws Exception {
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse response = new RsSkdMeldingResponse();
        response.setSendSkdMeldingTilTpsResponsene(Arrays.asList(standardSendSkdResponse));
        TpsfException tpsfException = new TpsfException(standardFeilmelding);

        when(tpsfApiService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(tpsfException);

        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        verify(identService, never()).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(tpsfResponseHandler).setErrorMessageToBestillingsProgress(any(TpsfException.class), any(BestillingProgress.class));
    }

    @Test
    public void opprettPersonerByKriterierAsync_sjekkAtIngenSigrunRequestIkkeGirNullPointException() {
        when(tpsfApiService.opprettIdenterTpsf(standardBestillingRequest_u1_t2_q3.getTpsf())).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfApiService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);

        standardBestillingRequest_u1_t2_q3.setSigrunRequest(null);
        dollyTpsfService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);
        verify(sigrunResponseHandler, times(0)).extractResponse(any());
    }
}