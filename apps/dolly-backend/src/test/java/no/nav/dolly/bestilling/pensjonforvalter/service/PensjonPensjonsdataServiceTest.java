package no.nav.dolly.bestilling.pensjonforvalter.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterHelper;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.dolly.service.TransaksjonMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.service.PensjonPensjonsdataService.mergePensjonforvalterResponses;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PensjonPensjonsdataServiceTest {

    private static final String IDENT = "11111111111";

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private PensjonforvalterConsumer pensjonforvalterConsumer;

    @Spy
    private PensjonforvalterHelper pensjonforvalterHelper =
            new PensjonforvalterHelper(new ErrorStatusDecoder(objectMapper),
                    objectMapper,
                    new TransaksjonMappingService(transaksjonMappingRepository, objectMapper));

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private PensjonPensjonsdataService pensjonPensjonsdataService;

    private static PensjonData.TpOrdning getTpOrdningWithYtelser(String ordning, List<PensjonData.TpYtelse> ytelser) {

        return PensjonData.TpOrdning.builder()
                .ordning(ordning)
                .ytelser(ytelser)
                .build();
    }

    static class PensjonforvalterClientTestUtil {

        static PensjonforvalterResponse getPensjonforvalterResponse(int httpStatusCode, String... miljoe) {

            var response = new PensjonforvalterResponse();
            var status = new ArrayList<PensjonforvalterResponse.ResponseEnvironment>();

            var statusResponse = new PensjonforvalterResponse.Response();
            statusResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus(httpStatusCode == 200 ? "OK" : "FEIL", httpStatusCode));

            Arrays.stream(miljoe).forEach(m -> status.add(new PensjonforvalterResponse.ResponseEnvironment(m, statusResponse)));

            response.setStatus(status);

            return response;
        }
    }

    @BeforeEach
    void setup() {
        when(mapperFacade.map(any(PdlPersonBolk.PersonBolk.class), eq(PensjonPersonRequest.class))).thenReturn(new PensjonPersonRequest());
        when(pensjonforvalterConsumer.opprettPerson(any(PensjonPersonRequest.class), anySet()))
                .thenReturn(Flux.just(new PensjonforvalterResponse()));
    }

    @Test
    void testLagreTpForhold_withOkResult() {

        var tp1 = getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        var tp2 = getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        PensjonData pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Set.of("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var progress = new BestillingProgress();
        var dbBestilling = Bestilling.builder().id(1L).build();
        progress.setBestilling(dbBestilling);

        when(pensjonforvalterConsumer.lagreTpForhold(any(PensjonTpForholdRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new PensjonforvalterResponse.ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new PensjonforvalterResponse.ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build())))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpYtelse(any(PensjonTpYtelseRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new PensjonforvalterResponse.ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new PensjonforvalterResponse.ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build())))
                        .build()));

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(PensjonTpForholdRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(PensjonTpYtelseRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpYtelseRequest());

        StepVerifier.create(pensjonPensjonsdataService.lagrePensjonsdata(bestilling, IDENT, bestilling.getEnvironments()))
                .assertNext(status -> {
                    assertThat(status, containsString("TpForhold#"));
                    assertThat(Arrays.asList(status.split("#")[1].split(",")), containsInAnyOrder("TEST1:OK", "TEST2:OK"));
                })
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_withOneFailedResult() {

        var tp1 = getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        var tp2 = getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        var pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Set.of("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var progress = new BestillingProgress();
        var dbBestilling = Bestilling.builder().id(1L).build();
        progress.setBestilling(dbBestilling);

        when(pensjonforvalterConsumer.lagreTpForhold(any(PensjonTpForholdRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new PensjonforvalterResponse.ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new PensjonforvalterResponse.ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build())))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpYtelse(any(PensjonTpYtelseRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new PensjonforvalterResponse.ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new PensjonforvalterResponse.ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("Intern feil", 500))
                                        .message("{ytelse2 feil on TEST2}")
                                        .build())))
                        .build()));

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(PensjonTpForholdRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(PensjonTpYtelseRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpYtelseRequest());

        StepVerifier.create(pensjonPensjonsdataService.lagrePensjonsdata(bestilling, IDENT, bestilling.getEnvironments()))
                .assertNext(status -> {
                    assertThat(status, containsString("TpForhold#"));
                    assertThat(Arrays.asList(status.split("#")[1].split(",")), containsInAnyOrder("TEST1:OK", "TEST2:Feil= ytelse2 feil on TEST2"));
                })
                .verifyComplete();
    }

    @Test
    void testLagreTpForhold_withException() {

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Set.of("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(PensjonData.builder()
                .tp(List.of(getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse())),
                        getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()))))
                .build());

        when(pensjonforvalterConsumer.lagreTpForhold(any(PensjonTpForholdRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new PensjonforvalterResponse.ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new PensjonforvalterResponse.ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("", 200))
                                        .build())
                        ))
                        .build()));

        when(pensjonforvalterConsumer.lagreTpYtelse(any(PensjonTpYtelseRequest.class)))
                .thenReturn(Flux.just(PensjonforvalterResponse.builder()
                        .status(List.of(
                                new PensjonforvalterResponse.ResponseEnvironment("TEST1", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("OK", 200))
                                        .build()),
                                new PensjonforvalterResponse.ResponseEnvironment("TEST2", PensjonforvalterResponse.Response.builder()
                                        .httpStatus(new PensjonforvalterResponse.HttpStatus("Internal Server Error", 500))
                                        .message(String.format("Klarte ikke å få TP-ytelse respons for %s i PESYS (pensjon)", "12345"))
                                        .build())))
                        .build()));


        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(PensjonTpForholdRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(PensjonTpYtelseRequest.class), any(MappingContext.class)))
                .thenReturn(new PensjonTpYtelseRequest());

        StepVerifier.create(pensjonPensjonsdataService.lagrePensjonsdata(bestilling, IDENT, bestilling.getEnvironments()))
                .assertNext(status -> {
                    assertThat(status, containsString("TpForhold#"));
                    assertThat(Arrays.asList(status.split("#")[1].split(",")), containsInAnyOrder("TEST1:OK",
                            "TEST2:Feil= Klarte ikke å få TP-ytelse respons for 12345 i PESYS (pensjon)"));
                })
                .verifyComplete();
    }

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
        assertThat(result.getStatus().getFirst().getMiljo(), is(equalTo(resultatMiljo)));
        assertThat(result.getStatus().getFirst().getResponse().getHttpStatus().getStatus(), is(equalTo(resultatStatus)));
    }

    // none empty new response list to none empty previous list with different env name
    @Test
    void testMergePensjonforvalterResponses_withDifferentEnvironments() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T2");

        var result = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(result.getStatus(), hasSize(2));
        assertThat(result.getStatus().getFirst().getMiljo(), is(equalTo("T1")));
        assertThat(result.getStatus().getFirst().getResponse().getHttpStatus().getStatus(), is(equalTo(200)));
        assertThat(result.getStatus().getLast().getMiljo(), is(equalTo("T2")));
        assertThat(result.getStatus().getLast().getResponse().getHttpStatus().getStatus(), is(equalTo(200)));
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
}