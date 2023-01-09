package no.nav.dolly.bestilling.pensjonforvalter;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse.ResponseEnvironment;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.mergePensjonforvalterResponses;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PensjonforvalterClientTest {

    @Mock
    private PensjonforvalterConsumer pensjonforvalterConsumer;

    @Mock
    private DollyPersonCache dollyPersonCache;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AccessToken accessToken;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private PensjonforvalterClient pensjonforvalterClient;

    @BeforeEach
    public void setup() {
        when(errorStatusDecoder.decodeThrowable(any())).thenReturn("Teknisk feil. Se logg!");
        when(mapperFacade.map(any(Person.class), eq(OpprettPersonRequest.class))).thenReturn(new OpprettPersonRequest());
        when(pensjonforvalterConsumer.getAccessToken()).thenReturn(Mono.just(accessToken));
        when(pensjonforvalterConsumer.opprettPerson(any(OpprettPersonRequest.class), anySet(), eq(accessToken)))
                .thenReturn(Flux.just(new PensjonforvalterResponse()));
    }

    // empty new response list to empty previous list
    // none empty new response list to empty previous list
    // empty new response list to none empty previous list
    @Test
    public void testMergePensjonforvalterResponses_withEmptyList() {
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
            "T1,500,T1,500,T1,500"})
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
    public void testMergePensjonforvalterResponses_withDifferentEnvironments() {
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
    public void testMergePensjonforvalterResponses_withMixedEnvironments_200_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse(200, "T3", "T1");

        var result = mergePensjonforvalterResponses(List.of(response1, response2));

        assertThat(result.getStatus(), hasSize(3));
        assertThat(result.getStatus().get(0).getMiljo(), is(equalTo("T1")));
        assertThat(result.getStatus().get(1).getMiljo(), is(equalTo("T2")));
        assertThat(result.getStatus().get(2).getMiljo(), is(equalTo("T3")));
    }

    @Test
    public void testMergePensjonforvalterResponses_withMixedEnvironments_200_til_500() {
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
    public void testMergePensjonforvalterResponses_withMixedEnvironments_500_til_200() {
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
    public void testMergePensjonforvalterResponses_withMixedEnvironments_500_til_500() {
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
    public void testLagreTpForhold_withOkResult() {
        PensjonData.TpOrdning tp1 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        PensjonData.TpOrdning tp2 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        PensjonData pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        RsDollyUtvidetBestilling bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Arrays.asList("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var person = Person.builder()
                .ident("000")
                .build();
        var dollyPerson = DollyPerson.builder()
                .hovedperson("000")
                .persondetaljer(List.of(person))
                .opprettetIPDL(true)
                .build();

        BestillingProgress progress = new BestillingProgress();
        progress.setBestilling(new Bestilling());

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Set.of("TEST1", "TEST2"));

        var test1EnvResponse = new PensjonforvalterResponse.Response();
        test1EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));
        var test2EnvResponse = new PensjonforvalterResponse.Response();
        test2EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));

        PensjonforvalterResponse lagreTpForholdResponse = new PensjonforvalterResponse();
        lagreTpForholdResponse.setStatus(List.of(
                new ResponseEnvironment("TEST1", test1EnvResponse),
                new ResponseEnvironment("TEST2", test2EnvResponse)
        ));

        when(pensjonforvalterConsumer.lagreTpForhold(any(LagreTpForholdRequest.class), eq(accessToken)))
                .thenReturn(Flux.just(lagreTpForholdResponse));

        PensjonforvalterResponse lagreTpYtelseResponse = new PensjonforvalterResponse();
        lagreTpYtelseResponse.setStatus(List.of(
                new ResponseEnvironment("TEST1", test1EnvResponse),
                new ResponseEnvironment("TEST2", test2EnvResponse)
        ));
        when(pensjonforvalterConsumer.lagreTpYtelse(any(LagreTpYtelseRequest.class), eq(accessToken)))
                .thenReturn(Flux.just(lagreTpYtelseResponse));

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(LagreTpForholdRequest.class))).thenReturn(new LagreTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(LagreTpYtelseRequest.class))).thenReturn(new LagreTpYtelseRequest());

        pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false);

        assertThat(progress.getPensjonforvalterStatus(), is(not(nullValue())));
        assertThat(progress.getPensjonforvalterStatus(), containsString("TEST1:OK"));
        assertThat(progress.getPensjonforvalterStatus(), containsString("TEST2:OK"));
    }

    @Test
    public void testLagreTpForhold_withOneFailedResult() {
        PensjonData.TpOrdning tp1 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        PensjonData.TpOrdning tp2 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        PensjonData pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        RsDollyUtvidetBestilling bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Arrays.asList("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var person = Person.builder()
                .ident("000")
                .build();
        var dollyPerson = DollyPerson.builder()
                .hovedperson("000")
                .persondetaljer(List.of(person))
                .opprettetIPDL(true)
                .build();

        BestillingProgress progress = new BestillingProgress();
        progress.setBestilling(new Bestilling());

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Set.of("TEST1", "TEST2"));

        var test1EnvResponse = new PensjonforvalterResponse.Response();
        test1EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));
        var test2EnvResponse = new PensjonforvalterResponse.Response();
        test2EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));

        PensjonforvalterResponse lagreTpForholdResponse = new PensjonforvalterResponse();
        lagreTpForholdResponse.setStatus(List.of(
                new ResponseEnvironment("TEST1", test1EnvResponse),
                new ResponseEnvironment("TEST2", test2EnvResponse)
        ));

        when(pensjonforvalterConsumer.lagreTpForhold(any(LagreTpForholdRequest.class), eq(accessToken)))
                .thenReturn(Flux.just(lagreTpForholdResponse));

        var test2EnvYtelseResponse = new PensjonforvalterResponse.Response();
        test2EnvYtelseResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 500));
        test2EnvYtelseResponse.setMessage("ytelse2 feil on TEST2");

        PensjonforvalterResponse lagreTpYtelseResponse = new PensjonforvalterResponse();
        lagreTpYtelseResponse.setStatus(List.of(
                new ResponseEnvironment("TEST1", test1EnvResponse),
                new ResponseEnvironment("TEST2", test2EnvYtelseResponse)
        ));
        when(pensjonforvalterConsumer.lagreTpYtelse(any(LagreTpYtelseRequest.class), eq(accessToken)))
                .thenReturn(Flux.just(lagreTpYtelseResponse));

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(LagreTpForholdRequest.class))).thenReturn(new LagreTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(LagreTpYtelseRequest.class))).thenReturn(new LagreTpYtelseRequest());
        when(errorStatusDecoder.getErrorText(any(HttpStatus.class), anyString())).thenReturn("Feil= " + test2EnvYtelseResponse.getMessage());

        pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false);

        assertThat(progress.getPensjonforvalterStatus(), is(not(nullValue())));
        assertThat(progress.getPensjonforvalterStatus(), containsString("TEST1:OK"));
        assertThat(progress.getPensjonforvalterStatus(), containsString("TEST2:Feil= ytelse2 feil on TEST2"));
    }

    @Test
    public void testLagreTpForhold_withException() {

        PensjonData.TpOrdning tp1 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("1111", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));
        PensjonData.TpOrdning tp2 = PensjonforvalterClientTestUtil.getTpOrdningWithYtelser("2222", List.of(new PensjonData.TpYtelse(), new PensjonData.TpYtelse()));

        PensjonData pensjonData = new PensjonData();
        pensjonData.setTp(Arrays.asList(tp1, tp2));

        RsDollyUtvidetBestilling bestilling = new RsDollyUtvidetBestilling();
        bestilling.setEnvironments(Arrays.asList("TEST1", "TEST2"));
        bestilling.setPensjonforvalter(pensjonData);

        var person = Person.builder()
                .ident("000")
                .build();
        var dollyPerson = DollyPerson.builder()
                .hovedperson("000")
                .persondetaljer(List.of(person))
                .opprettetIPDL(true)
                .build();

        BestillingProgress progress = new BestillingProgress();
        progress.setBestilling(new Bestilling());

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Set.of("TEST1", "TEST2"));

        var test1EnvResponse = new PensjonforvalterResponse.Response();
        test1EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));
        var test2EnvResponse = new PensjonforvalterResponse.Response();
        test2EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));

        PensjonforvalterResponse lagreTpForholdResponse = new PensjonforvalterResponse();
        lagreTpForholdResponse.setStatus(List.of(
                new ResponseEnvironment("TEST1", test1EnvResponse),
                new ResponseEnvironment("TEST2", test2EnvResponse)
        ));

        when(pensjonforvalterConsumer.lagreTpForhold(any(LagreTpForholdRequest.class), eq(accessToken)))
                .thenReturn(Flux.just(lagreTpForholdResponse));

        var test2EnvYtelseResponse = new PensjonforvalterResponse.Response();
        test2EnvYtelseResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("Internal Server Error", 500));
        test2EnvYtelseResponse.setMessage(String.format("Klarte ikke å få TP-ytelse respons for %s i PESYS (pensjon)", "12345"));

        var lagreTpYtelseResponse = PensjonforvalterResponse.builder()
                .status(List.of(
                        new ResponseEnvironment("TEST1", test1EnvResponse),
                        new ResponseEnvironment("TEST2", test2EnvYtelseResponse)))
                .build();

        when(pensjonforvalterConsumer.lagreTpYtelse(any(LagreTpYtelseRequest.class), eq(accessToken))).thenReturn(Flux.just(lagreTpYtelseResponse));

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(LagreTpForholdRequest.class))).thenReturn(new LagreTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(LagreTpYtelseRequest.class))).thenReturn(new LagreTpYtelseRequest());
        when(errorStatusDecoder.getErrorText(any(HttpStatus.class), anyString())).thenReturn(test2EnvYtelseResponse.getMessage());

        pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false);

        assertThat(progress.getPensjonforvalterStatus(), containsString("Klarte ikke å få TP-ytelse respons"));
    }

    public static class PensjonforvalterClientTestUtil {
        public static PensjonData.TpOrdning getTpOrdning(String ordning) {
            PensjonData.TpOrdning tp = new PensjonData.TpOrdning();
            tp.setOrdning(ordning);
            return tp;
        }

        public static PensjonData.TpOrdning getTpOrdningWithYtelser(String ordning, List<PensjonData.TpYtelse> ytelser) {
            PensjonData.TpOrdning tp = new PensjonData.TpOrdning();
            tp.setOrdning(ordning);
            tp.setYtelser(ytelser);
            return tp;
        }

        public static PensjonforvalterResponse getPensjonforvalterResponse(int httpStatusCode, String... miljoe) {
            var response = new PensjonforvalterResponse();
            var status = new ArrayList<ResponseEnvironment>();

            var statusResponse = new PensjonforvalterResponse.Response();
            statusResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus(httpStatusCode == 200 ? "OK" : "FEIL", httpStatusCode));

            Arrays.stream(miljoe).forEach(m -> status.add(new ResponseEnvironment(m, statusResponse)));

            response.setStatus(status);

            return response;
        }
    }
}
