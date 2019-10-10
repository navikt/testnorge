package no.nav.dolly.appservices.tpsf.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.ServiceRoutineResponseStatus;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.IdentStatus;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class DollyBestillingServiceTest {

    private static final Long GRUPPE_ID = 1L;
    private static final Long BESTILLING_ID = 2L;
    private static final String SUCCESS_CODE_TPS = "OK";
    private static final String FAIL_CODE_TPS = "08";
    private static final String INNVANDRING_CREATE_NAVN = "InnvandringCreate";
    private static final String IDENT_1 = "10101012345";
    private static final String IDENT_2 = "24301012345";
    private static final String IDENT_3 = "56101012345";
    private static final String FEILMELDING = "feil";
    private static final String TPS_FEEDBACK = "feedback";
    private static final List<String> STANDARD_IDENTER = asList(IDENT_1, IDENT_2, IDENT_3);
    private static final String STATUS = "Tull";

    private Map<String, String> status_SuccU1T2_FailQ3;
    private RsDollyBestillingRequest standardBestillingRequest_u1_t2_q3;
    private Bestilling standardNyBestilling;
    private Testgruppe standardGruppe;
    private SendSkdMeldingTilTpsResponse standardSendSkdResponse;
    private ServiceRoutineResponseStatus serviceRoutineResponseStatus;

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

    @Mock
    private List<ClientRegister> clientRegisters;

    @InjectMocks
    private DollyBestillingService dollyBestillingService;

    @Mock
    private SigrunStubResponseHandler responseHandler;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private Cache cache;

    @Mock
    private RsSkdMeldingResponse skdMeldingResponse;

    @Before
    public void setup() {
        standardGruppe = new Testgruppe();

        standardSendSkdResponse = SendSkdMeldingTilTpsResponse.builder()
                .personId(IDENT_1)
                .skdmeldingstype(INNVANDRING_CREATE_NAVN)
                .status(singletonMap("u2", "OK"))
                .build();

        serviceRoutineResponseStatus = ServiceRoutineResponseStatus.builder()
                .serviceRutinenavn("endre_spraakkode")
                .personId(IDENT_1)
                .status(singletonMap("u2", "OK"))
                .build();

        standardNyBestilling = Bestilling.builder().id(BESTILLING_ID).build();

        status_SuccU1T2_FailQ3 = new HashMap<>();
        status_SuccU1T2_FailQ3.put("u1", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("t2", SUCCESS_CODE_TPS);
        status_SuccU1T2_FailQ3.put("q3", FAIL_CODE_TPS);

        standardBestillingRequest_u1_t2_q3 = new RsDollyBestillingRequest();
        standardBestillingRequest_u1_t2_q3.setTpsf(new RsTpsfUtvidetBestilling());

        standardBestillingRequest_u1_t2_q3.setEnvironments(asList("u1", "t2", "q3"));
        standardBestillingRequest_u1_t2_q3.setAntall(1);

        when(bestillingService.isStoppet(anyLong())).thenReturn(false);
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        when(mapperFacade.map(any(RsTpsfUtvidetBestilling.class), eq(TpsfBestilling.class))).thenReturn(new TpsfBestilling());
    }

    @Test
    public void opprettPersonerByKriterierAsync_bestillingBlirSattFerdigNaarExceptionKastesUnderOppretting() {
        when(tpsfService.opprettIdenterTpsf(any(TpsfBestilling.class))).thenReturn(STANDARD_IDENTER);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);

        dollyBestillingService.opprettPersonerByKriterierAsync(GRUPPE_ID, standardBestillingRequest_u1_t2_q3, standardNyBestilling);

        assertThat(standardNyBestilling.isFerdig(), is(true));
        verify(bestillingService, times(2)).saveBestillingToDB(standardNyBestilling);
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerAlleMiljoeneSomErsuksessfulleSendtTilTPSTilBestilllingProgress() {
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        RsSkdMeldingResponse skdMeldingResponse = RsSkdMeldingResponse.builder()
                .sendSkdMeldingTilTpsResponsene(singletonList(standardSendSkdResponse))
                .serviceRoutineStatusResponsene(singletonList(serviceRoutineResponseStatus))
                .build();

        when(tpsfService.opprettIdenterTpsf(any(TpsfBestilling.class))).thenReturn(STANDARD_IDENTER);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(skdMeldingResponse);
        when(tpsfResponseHandler.extractTPSFeedback(anyList())).thenReturn(TPS_FEEDBACK);

        dollyBestillingService.opprettPersonerByKriterierAsync(GRUPPE_ID, standardBestillingRequest_u1_t2_q3, standardNyBestilling);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService).saveIdentTilGruppe(IDENT_1, standardGruppe);
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

        RsSkdMeldingResponse response = RsSkdMeldingResponse.builder()
                .sendSkdMeldingTilTpsResponsene(singletonList(standardSendSkdResponse))
                .serviceRoutineStatusResponsene(singletonList(serviceRoutineResponseStatus))
                .build();

        TpsfException tpsfException = new TpsfException(FEILMELDING);

        when(tpsfService.opprettIdenterTpsf(any(TpsfBestilling.class))).thenReturn(STANDARD_IDENTER);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenReturn(response).thenThrow(tpsfException);
        when(tpsfResponseHandler.extractTPSFeedback(anyList())).thenReturn(TPS_FEEDBACK);

        dollyBestillingService.opprettPersonerByKriterierAsync(GRUPPE_ID, standardBestillingRequest_u1_t2_q3, standardNyBestilling);

        ArgumentCaptor<BestillingProgress> argumentCaptor = ArgumentCaptor.forClass(BestillingProgress.class);

        verify(identService, times(1)).saveIdentTilGruppe(IDENT_1, standardGruppe);
        verify(bestillingProgressRepository, times(4)).save(argumentCaptor.capture());
        verify(tpsfResponseHandler).setErrorMessageToBestillingsProgress(any(TpsfException.class), any(BestillingProgress.class));

        List<BestillingProgress> bestillingProgresses = argumentCaptor.getAllValues();
        BestillingProgress bestillingProgressOK = bestillingProgresses.get(0);

        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("u1"), is(true));
        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("t2"), is(true));
        assertThat(bestillingProgressOK.getTpsfSuccessEnv().contains("q3"), is(false));
    }

    @Test
    public void opprettPersonerByKriterierAsync_lagrerFeilIProgressHvisSendingAvIdenterTilTpsMiljoFeiler() {
        standardSendSkdResponse.setStatus(status_SuccU1T2_FailQ3);

        TpsfException tpsfException = new TpsfException(FEILMELDING);

        when(tpsfService.opprettIdenterTpsf(any(TpsfBestilling.class))).thenReturn(STANDARD_IDENTER);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(tpsfException);

        dollyBestillingService.opprettPersonerByKriterierAsync(GRUPPE_ID, standardBestillingRequest_u1_t2_q3, standardNyBestilling);

        verify(identService, never()).saveIdentTilGruppe(IDENT_1, standardGruppe);
        verify(tpsfResponseHandler).setErrorMessageToBestillingsProgress(any(TpsfException.class), any(BestillingProgress.class));
    }

    @Test
    public void opprettPersonerByKriterierAsync_sjekkAtIngenSigrunRequestIkkeGirNullPointException() {
        when(tpsfService.opprettIdenterTpsf(any(TpsfBestilling.class))).thenReturn(STANDARD_IDENTER);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(any(), any())).thenThrow(TpsfException.class);

        standardBestillingRequest_u1_t2_q3.setSigrunstub(null);
        dollyBestillingService.opprettPersonerByKriterierAsync(GRUPPE_ID, standardBestillingRequest_u1_t2_q3, standardNyBestilling);
        verify(responseHandler, times(0)).extractResponse(any());
    }

    @Test
    public void gjenopprettPersonerIMiljoer() {

        when(bestillingProgressRepository.findBestillingProgressByBestillingIdOrderByBestillingId(BESTILLING_ID)).thenReturn(
                singletonList(BestillingProgress.builder().ident(IDENT_1).build()));
        when(tpsfService.hentTilhoerendeIdenter(singletonList(IDENT_1))).thenReturn(singletonList(IDENT_1));
        when(tpsfService.sendIdenterTilTpsFraTPSF(anyList(), anyList())).thenReturn(skdMeldingResponse);

        dollyBestillingService.gjenopprettBestillingAsync(
                Bestilling.builder().id(BESTILLING_ID)
                        .opprettetFraId(BESTILLING_ID)
                        .miljoer("t2,t3").build());

        verify(bestillingService, times(4)).isStoppet(BESTILLING_ID);
        verify(tpsfService).hentTilhoerendeIdenter(anyList());
        verify(tpsfService).sendIdenterTilTpsFraTPSF(anyList(), anyList());
        verify(tpsfResponseHandler).extractTPSFeedback(anyList());
    }

    @Test
    public void opprettPersonerByKriterierFraIdenterAsync_bestillingOk() {

        RsDollyBestillingFraIdenterRequest bestillingFraIdenterRequest = new RsDollyBestillingFraIdenterRequest();
        bestillingFraIdenterRequest.getOpprettFraIdenter().addAll(STANDARD_IDENTER);
        bestillingFraIdenterRequest.setEnvironments(asList("u1", "t2", "q3"));

        RsSkdMeldingResponse response = RsSkdMeldingResponse.builder()
                .sendSkdMeldingTilTpsResponsene(singletonList(standardSendSkdResponse))
                .serviceRoutineStatusResponsene(singletonList(serviceRoutineResponseStatus))
                .build();
        when(tpsfService.opprettIdenterTpsf(any(TpsfBestilling.class))).thenReturn(STANDARD_IDENTER);
        when(testgruppeService.fetchTestgruppeById(GRUPPE_ID)).thenReturn(standardGruppe);
        when(tpsfService.sendIdenterTilTpsFraTPSF(anyList(), anyList())).thenReturn(response);

        when(tpsfService.checkEksisterendeIdenter(STANDARD_IDENTER)).thenReturn(
                CheckStatusResponse.builder().statuser(
                        asList(IdentStatus.builder().available(true).ident(IDENT_1).status(STATUS).build(),
                                IdentStatus.builder().available(true).ident(IDENT_2).status(STATUS).build(),
                                IdentStatus.builder().available(true).ident(IDENT_3).status(STATUS).build()))
                        .build());

        dollyBestillingService.opprettPersonerFraIdenterMedKriterierAsync(GRUPPE_ID, bestillingFraIdenterRequest, standardNyBestilling);

        assertThat(standardNyBestilling.isFerdig(), is(true));
        verify(bestillingService, times(4)).saveBestillingToDB(standardNyBestilling);
    }
}