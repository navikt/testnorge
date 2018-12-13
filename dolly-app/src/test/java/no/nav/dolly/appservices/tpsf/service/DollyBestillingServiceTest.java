package no.nav.dolly.appservices.tpsf.service;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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

import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@RunWith(MockitoJUnitRunner.class)
public class DollyBestillingServiceTest {

    private static final String SUCCESS_CODE_TPS = "OK";
    private static final String FAIL_CODE_TPS = "08";
    private static final String INNVANDRING_CREATE_NAVN = "InnvandringCreate";

    private Map<String, String> status_SuccU1T2_FailQ3;
    private RsDollyBestillingsRequest standardBestillingRequest_u1_t2_q3;
    private RsTpsfBestilling tpsfReqEmpty = new RsTpsfBestilling();
    private Bestilling standardNyBestilling;
    private Testgruppe standardGruppe = new Testgruppe();
    private SendSkdMeldingTilTpsResponse standardSendSkdResponse;
    private Long standardGruppeId = 1L;
    private Long bestillingsId = 2L;
    private String standardHovedident = "10";
    private String standardFeilmelding = "feil";
    private String standardTpsFeedback = "feedback";
    private List<String> standardIdenter = Arrays.asList(standardHovedident, "34", "56");

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private TpsfService tpsfService;

    @Mock
    private IdentService identService;

    @Mock
    private TestgruppeService testgruppeService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private TpsfResponseHandler tpsfResponseHandler;

    @InjectMocks
    private DollyBestillingService dollyBestillingService;

    @Mock
    private SigrunStubResponseHandler responseHandler;

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
    }

    @Test
    public void opprettPersonerByKriterierAsync_bestillingBlirSattFerdigNaarExceptionKastesUnderOppretting() {
        when(tpsfService.opprettIdenterTpsf(standardBestillingRequest_u1_t2_q3.getTpsf())).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);

        dollyBestillingService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        assertThat(standardNyBestilling.isFerdig(), is(true));
        verify(bestillingService, times(2)).saveBestillingToDB(standardNyBestilling);
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerAlleMiljoeneSomErsuksessfulleSendtTilTPSTilBestilllingProgress() {
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse skdMeldingResponse = new RsSkdMeldingResponse();
        skdMeldingResponse.setSendSkdMeldingTilTpsResponsene(singletonList(standardSendSkdResponse));

        when(tpsfService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(skdMeldingResponse);
        when(tpsfResponseHandler.extractTPSFeedback(anyList())).thenReturn(standardTpsFeedback);

        dollyBestillingService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(bestillingProgressRepository, times(2)).save(argumentCaptor.capture());

        BestillingProgress bestillingProgress = argumentCaptor.getValue();

        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("u1"), is(true));
        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("t2"), is(true));
        assertThat(bestillingProgress.getTpsfSuccessEnv().contains("q3"), is(false));
        assertThat(bestillingProgress.getFeil().contains("q3"), is(true));
    }

    @Test
    public void opprettPersonerByKriterierAsync_hvisFlereIdenterBestillesOgEnFeilerOgEnOkSaaEtBestillingProgressObjMedFeilOgEtMedOk() {
        int bestiltAntallIdenter = 2;
        standardBestillingRequest_u1_t2_q3.setAntall(bestiltAntallIdenter);
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse response = new RsSkdMeldingResponse();
        response.setSendSkdMeldingTilTpsResponsene(singletonList(standardSendSkdResponse));

        TpsfException tpsfException = new TpsfException(standardFeilmelding);

        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(response).thenThrow(tpsfException);
        when(tpsfResponseHandler.extractTPSFeedback(anyList())).thenReturn(standardTpsFeedback);

        dollyBestillingService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

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
        response.setSendSkdMeldingTilTpsResponsene(singletonList(standardSendSkdResponse));
        TpsfException tpsfException = new TpsfException(standardFeilmelding);

        when(tpsfService.opprettIdenterTpsf(tpsfReqEmpty)).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(tpsfException);

        dollyBestillingService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);

        verify(identService, never()).saveIdentTilGruppe(standardHovedident, standardGruppe);
        verify(tpsfResponseHandler).setErrorMessageToBestillingsProgress(any(TpsfException.class), any(BestillingProgress.class));
    }

    @Test
    public void opprettPersonerByKriterierAsync_sjekkAtIngenSigrunRequestIkkeGirNullPointException() {
        when(tpsfService.opprettIdenterTpsf(standardBestillingRequest_u1_t2_q3.getTpsf())).thenReturn(standardIdenter);
        when(testgruppeService.fetchTestgruppeById(standardGruppeId)).thenReturn(standardGruppe);
        when(bestillingService.fetchBestillingById(bestillingsId)).thenReturn(standardNyBestilling);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);

        standardBestillingRequest_u1_t2_q3.setSigrunstub(null);
        dollyBestillingService.opprettPersonerByKriterierAsync(standardGruppeId, standardBestillingRequest_u1_t2_q3, bestillingsId);
        verify(responseHandler, times(0)).extractResponse(any());
    }
}