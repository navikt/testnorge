package no.nav.dolly.bestilling.pensjonforvalter;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse.ResponseEnvironment;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.mergePensjonforvalterResponses;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PensjonforvalterClientTest {

    private static final String IDENT = "11111111111";

    @Mock
    private PensjonforvalterConsumer pensjonforvalterConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private PdlDataConsumer pdlDataConsumer;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private PensjonforvalterClient pensjonforvalterClient;

    @BeforeEach
    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
        when(mapperFacade.map(any(PdlPersonBolk.PersonBolk.class), eq(PensjonPersonRequest.class))).thenReturn(new PensjonPersonRequest());
        when(accessToken.getTokenValue()).thenReturn("123");
        when(pensjonforvalterConsumer.opprettPerson(any(PensjonPersonRequest.class), anySet()))
                .thenReturn(Flux.just(new PensjonforvalterResponse()));

        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .ident(IDENT)
                                .person(new PdlPerson.Person())
                                .build()))
                        .build())
                .build();
        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(pdlPersonBolk));
    }

    // empty new response list to empty previous list
    // none empty new response list to empty previous list
    // empty new response list to none empty previous list
    @Test
    void testMergePensjonforvalterResponses_withEmptyList() {
        var response1 = new PensjonforvalterResponse();
        response1.setStatus(new ArrayList<>());

        var response2 = new PensjonforvalterResponse();
        response2.setStatus(new ArrayList<>());

        var resultat = mergePensjonforvalterResponses(List.of(response1, response2));
        assertThat(resultat.getStatus(), is(empty()));

        response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T1");

        response2 = new PensjonforvalterResponse();
        response2.setStatus(new ArrayList<>());

        resultat = mergePensjonforvalterResponses(List.of(response1, response2));
        assertThat(resultat.getStatus(), hasSize(1));

        response1 = new PensjonforvalterResponse();
        response1.setStatus(new ArrayList<>());

        response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T2");

        resultat = mergePensjonforvalterResponses(List.of(response1, response2));
        assertThat(resultat.getStatus(), hasSize(1));
    }

    @ParameterizedTest
    @CsvSource({
            "T1,200,T1,200,T1,200",
            "T1,200,T1,500,T1,500",
            "T1,500,T1,200,T1,500",
            "T1,500,T1,500,T1,500" })
        // none empty new response list (with status 200) to none empty previous list with same env name and previous status of 200
        // none empty new response list (with status 200) to none empty previous list with same env name and previous status of 500
        // none empty new response list (with status 500) to none empty previous list with same env name and previous status of 200
        // none empty new response list (with status 500) to none empty previous list with same env name and previous status of 500
    void testMergePensjonforvalterResponses_withSameEnvironments_200_til_200(String miljo1, int status1, String miljo2, int status2, String resultatMiljo, int resultatStatus) {

        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(status1, miljo1);
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(status2, miljo2);

        var result = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(result.getStatus(), hasSize(1));
        assertThat(result.getStatus().get(0).getMiljo(), is(equalTo(resultatMiljo)));
        assertThat(result.getStatus().get(0).getResponse().getHttpStatus().getStatus(), is(equalTo(resultatStatus)));
    }

    // none empty new response list to none empty previous list with different env name
    @Test
    void testMergePensjonforvalterResponses_withDifferentEnvironments() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T2");

        var result = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(result.getStatus(), hasSize(2));
        assertThat(result.getStatus().get(0).getMiljo(), is(equalTo("T1")));
        assertThat(result.getStatus().get(0).getResponse().getHttpStatus().getStatus(), is(equalTo(200)));
        assertThat(result.getStatus().get(1).getMiljo(), is(equalTo("T2")));
        assertThat(result.getStatus().get(1).getResponse().getHttpStatus().getStatus(), is(equalTo(200)));
    }

    // none empty new reponse list with 2 env (status 200), previous list with 2 env but 1 is same, 1 is different (status 200)
    // none empty new reponse list with 2 env (status 200), previous list with 2 env but 1 is same, 1 is different (status 500)
    // none empty new reponse list with 2 env (status 500), previous list with 2 env but 1 is same, 1 is different (status 200)
    // none empty new reponse list with 2 env (status 500), previous list with 2 env but 1 is same, 1 is different (status 500)
    @Test
    void testMergePensjonforvalterResponses_withMixedEnvironments_200_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T3", "T1");

        var result = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(result.getStatus(), hasSize(3));
        assertThat(result.getStatus().get(0).getMiljo(), is(equalTo("T1")));
        assertThat(result.getStatus().get(1).getMiljo(), is(equalTo("T2")));
        assertThat(result.getStatus().get(2).getMiljo(), is(equalTo("T3")));
    }

    @Test
    void testMergePensjonforvalterResponses_withMixedEnvironments_200_til_500() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(500, "T3", "T1");

        var resultat = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(resultat.getStatus(), hasSize(3));
        assertThat(resultat.getStatus().get(0).getMiljo(), is(equalTo("T1")));
        assertThat(resultat.getStatus().get(0).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
        assertThat(resultat.getStatus().get(1).getMiljo(), is(equalTo("T2")));
        assertThat(resultat.getStatus().get(1).getResponse().getHttpStatus().getStatus(), is(equalTo(200)));
        assertThat(resultat.getStatus().get(2).getMiljo(), is(equalTo("T3")));
        assertThat(resultat.getStatus().get(2).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
    }

    @Test
    void testMergePensjonforvalterResponses_withMixedEnvironments_500_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(500, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T3", "T1");

        var resultat = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(resultat.getStatus(), hasSize(3));
        assertThat(resultat.getStatus().get(0).getMiljo(), is(equalTo("T1")));
        assertThat(resultat.getStatus().get(0).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
        assertThat(resultat.getStatus().get(1).getMiljo(), is(equalTo("T2")));
        assertThat(resultat.getStatus().get(1).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
        assertThat(resultat.getStatus().get(2).getMiljo(), is(equalTo("T3")));
        assertThat(resultat.getStatus().get(2).getResponse().getHttpStatus().getStatus(), is(equalTo(200)));
    }

    @Test
    void testMergePensjonforvalterResponses_withMixedEnvironments_500_til_500() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(500, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(500, "T3", "T1");

        var resultat = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(resultat.getStatus(), hasSize(3));
        assertThat(resultat.getStatus().get(0).getMiljo(), is(equalTo("T1")));
        assertThat(resultat.getStatus().get(0).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
        assertThat(resultat.getStatus().get(1).getMiljo(), is(equalTo("T2")));
        assertThat(resultat.getStatus().get(1).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
        assertThat(resultat.getStatus().get(2).getMiljo(), is(equalTo("T3")));
        assertThat(resultat.getStatus().get(2).getResponse().getHttpStatus().getStatus(), is(equalTo(500)));
    }

    @Test
    void testLagreTpForhold_withOkResult() {

        var tp1 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        var tp2 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        PensjonData pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Set.of("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var dollyPerson = DollyPerson.builder()
                .ident(IDENT)
                .build();

        var progress = new BestillingProgress();
        var dbBestilling = Bestilling.builder().id(1L).build();
        progress.setBestilling(dbBestilling);

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Mono.just(Set.of("TEST1", "TEST2")));

        when(pensjonforvalterConsumer.opprettPerson(any(PensjonPersonRequest.class), anySet()))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(ResponseEnvironment.builder()
                                .miljo("TEST1")
                                .response(PensjonforvalterResponse.Response.builder()
                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                .status(200)
                                                .build())
                                        .build())
                                .build()))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpForhold(any(PensjonTpForholdRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build())))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpYtelse(any(PensjonTpYtelseRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build())))
                        .build()));

        when(mapperFacade.map(any(PdlPersonBolk.PersonBolk.class), eq(PensjonPersonRequest.class)))
                .thenReturn(PensjonPersonRequest.builder()
                        .fnr(IDENT)
                        .build());
        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(PensjonTpForholdRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(PensjonTpYtelseRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpYtelseRequest());
        when(pdlDataConsumer.getPersoner(anyList())).thenReturn(Flux.empty());
        when(pensjonforvalterConsumer.hentSamboer(anyString(), anyString())).thenReturn(Flux.empty());

        StepVerifier.create(pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0).split("#")[0], is(equalTo("PensjonForvalter")));
                    assertThat(Arrays.asList(statusCaptor.getAllValues().get(0).split("#")[1].split(",")),
                            containsInAnyOrder("TEST1:Info= Oppretting startet mot PESYS ...", "TEST2:Info= Oppretting startet mot PESYS ..."));
                    assertThat(statusCaptor.getAllValues().get(1), is(CoreMatchers.equalTo("PensjonForvalter#TEST1:OK$TpForhold#TEST2:OK,TEST1:OK")));
                })
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_withOneFailedResult() {

        var tp1 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        var tp2 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        var pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Set.of("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var dollyPerson = DollyPerson.builder()
                .ident(IDENT)
                .build();

        var progress = new BestillingProgress();
        var dbBestilling = Bestilling.builder().id(1L).build();
        progress.setBestilling(dbBestilling);

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Mono.just(Set.of("TEST1", "TEST2")));

        when(pensjonforvalterConsumer.opprettPerson(any(PensjonPersonRequest.class), anySet()))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(ResponseEnvironment.builder()
                                        .miljo("TEST1")
                                        .response(PensjonforvalterResponse.Response.builder()
                                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                        .status(200)
                                                        .build())
                                                .build())
                                        .build(),
                                ResponseEnvironment.builder()
                                        .miljo("TEST2")
                                        .response(PensjonforvalterResponse.Response.builder()
                                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                        .status(200)
                                                        .build())
                                                .build())
                                        .build()))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpForhold(any(PensjonTpForholdRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build())))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpYtelse(any(PensjonTpYtelseRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("Intern feil", 500))
                                        .message("{ytelse2 feil on TEST2}")
                                        .build())))
                        .build()));

        when(mapperFacade.map(any(PdlPersonBolk.PersonBolk.class), eq(PensjonPersonRequest.class)))
                .thenReturn(PensjonPersonRequest.builder()
                        .fnr(IDENT)
                        .build());
        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(PensjonTpForholdRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(PensjonTpYtelseRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpYtelseRequest());
        when(errorStatusDecoder.getErrorText(any(), any()))
                .thenCallRealMethod();
        when(pdlDataConsumer.getPersoner(anyList())).thenReturn(Flux.empty());
        when(pensjonforvalterConsumer.hentSamboer(anyString(), anyString())).thenReturn(Flux.empty());

        StepVerifier.create(pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0).split("#")[0], is(equalTo("PensjonForvalter")));
                    assertThat(Arrays.asList(statusCaptor.getAllValues().get(0).split("#")[1].split(",")),
                            containsInAnyOrder("TEST1:Info= Oppretting startet mot PESYS ...", "TEST2:Info= Oppretting startet mot PESYS ..."));
                    assertThat(statusCaptor.getAllValues().get(1), is(CoreMatchers.equalTo("PensjonForvalter#TEST1:OK,TEST2:OK$TpForhold#TEST2:Feil= ytelse2 feil on TEST2,TEST1:OK")));
                })
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_withException() {

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Set.of("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(PensjonData.builder()
                .tp(List.of(PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse())),
                        PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()))))
                .build());

        var dollyPerson = DollyPerson.builder()
                .ident(IDENT)
                .build();

        var dbBestilling = Bestilling.builder().id(1L).build();
        var progress = BestillingProgress.builder()
                .bestilling(dbBestilling)
                .build();

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Mono.just(Set.of("TEST1", "TEST2")));

        when(pensjonforvalterConsumer.opprettPerson(any(PensjonPersonRequest.class), anySet()))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(ResponseEnvironment.builder()
                                        .miljo("TEST1")
                                        .response(PensjonforvalterResponse.Response.builder()
                                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                        .status(200)
                                                        .build())
                                                .build())
                                        .build(),
                                ResponseEnvironment.builder()
                                        .miljo("TEST2")
                                        .response(PensjonforvalterResponse.Response.builder()
                                                .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                        .status(200)
                                                        .build())
                                                .build())
                                        .build()))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpForhold(any(PensjonTpForholdRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("", 200))
                                        .build())
                        ))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpYtelse(any(PensjonTpYtelseRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("Internal Server Error", 500))
                                        .message(String.format("Klarte ikke å få TP-ytelse respons for %s i PESYS (pensjon)", "12345"))
                                        .build())))
                        .build()));

        when(mapperFacade.map(any(PdlPersonBolk.PersonBolk.class), eq(PensjonPersonRequest.class)))
                .thenReturn(PensjonPersonRequest.builder()
                        .fnr(IDENT)
                        .build());
        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(PensjonTpForholdRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(PensjonTpYtelseRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpYtelseRequest());
        when(errorStatusDecoder.getErrorText(eq(HttpStatus.INTERNAL_SERVER_ERROR), anyString()))
                .thenReturn("Feil= Klarte ikke å få TP-ytelse respons for 12345 i PESYS (pensjon)");
        when(pdlDataConsumer.getPersoner(anyList())).thenReturn(Flux.empty());
        when(pensjonforvalterConsumer.hentSamboer(anyString(), anyString())).thenReturn(Flux.empty());

        StepVerifier.create(pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().get(0).split("#")[0], is(equalTo("PensjonForvalter")));
                    assertThat(Arrays.asList(statusCaptor.getAllValues().get(0).split("#")[1].split(",")),
                            containsInAnyOrder("TEST1:Info= Oppretting startet mot PESYS ...", "TEST2:Info= Oppretting startet mot PESYS ..."));
                    assertThat(statusCaptor.getAllValues().get(1),
                            is(CoreMatchers.equalTo("PensjonForvalter#TEST1:OK,TEST2:OK$" +
                                    "TpForhold#TEST2:Feil= Klarte ikke å få TP-ytelse respons for 12345 i PESYS (pensjon),TEST1:OK")));
                })
                .verifyComplete();
    }

    static class PensjonforvalterClientTestUtil {

        static PensjonData.TpOrdning getTpOrdning(String ordning) {
            PensjonData.TpOrdning tp = new PensjonData.TpOrdning();
            tp.setOrdning(ordning);
            return tp;
        }

        static PensjonData.TpOrdning getTpOrdningWithYtelser(String ordning, List<PensjonData.TpYtelse> ytelser) {
            PensjonData.TpOrdning tp = new PensjonData.TpOrdning();
            tp.setOrdning(ordning);
            tp.setYtelser(ytelser);
            return tp;
        }

        static PensjonforvalterResponse getPensjonforvalterResponse(int httpStatusCode, String... miljoe) {

            var response = new PensjonforvalterResponse();
            var status = new ArrayList<ResponseEnvironment>();

            var statusResponse = new PensjonforvalterResponse.Response();
            statusResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus(httpStatusCode == 200 ? "OK" : "FEIL", httpStatusCode));

            Arrays.stream(miljoe).forEach(m -> status.add(new ResponseEnvironment(m, statusResponse)));

            response.setStatus(status);

            return response;
        }
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "{MESSAGE},REASON,Feil= MESSAGE",
                    "null,REASON,Feil= REASON",
                    "null,null,Feil= 500 INTERNAL_SERVER_ERROR"
            },
            nullValues = {
                    "null"
            }
    )
    void testGetError(String message, String reason, String expected) {

        when(errorStatusDecoder.getErrorText(any(), any()))
                .thenCallRealMethod();
        var entry = ResponseEnvironment
                .builder()
                .miljo("ENV")
                .response(
                        PensjonforvalterResponse.Response
                                .builder()
                                .path("/test")
                                .message(message)
                                .httpStatus(
                                        PensjonforvalterResponse.HttpStatus
                                                .builder()
                                                .status(500)
                                                .reasonPhrase(reason)
                                                .build())
                                .build())
                .build();
        var error = pensjonforvalterClient.getError(entry);
        assertThat(error, is(expected));

    }

}