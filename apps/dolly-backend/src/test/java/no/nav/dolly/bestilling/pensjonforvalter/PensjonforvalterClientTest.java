package no.nav.dolly.bestilling.pensjonforvalter;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.mergePensjonforvalterResponses;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private PensjonforvalterClient pensjonforvalterClient;

    // empty new response list to empty previous list
    // none empty new response list to empty previous list
    // empty new response list to none empty previous list
    @Test
    public void testMergePensjonforvalterResponses_withEmptyList() {
        var response1 = new PensjonforvalterResponse();
        response1.setStatus(new ArrayList<>());

        var response2 = new PensjonforvalterResponse();
        response2.setStatus(new ArrayList<>());

        mergePensjonforvalterResponses(response1, response2);
        assertThat("list for response2 should be empty", response2.getStatus().isEmpty());

        response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1");

        response2 = new PensjonforvalterResponse();
        response2.setStatus(new ArrayList<>());

        mergePensjonforvalterResponses(response1, response2);
        assertThat("size for response2 should be 1", response2.getStatus().size() == 1);

        response1 = new PensjonforvalterResponse();
        response1.setStatus(new ArrayList<>());

        response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T2");

        mergePensjonforvalterResponses(response1, response2);
        assertThat("size for response2 should be 1", response2.getStatus().size() == 1);
    }

    // none empty new response list (with status 200) to none empty previous list with same env name and previous status of 200
    // none empty new response list (with status 200) to none empty previous list with same env name and previous status of 500
    // none empty new response list (with status 500) to none empty previous list with same env name and previous status of 200
    // none empty new response list (with status 500) to none empty previous list with same env name and previous status of 500
    @Test
    public void testMergePensjonforvalterResponses_withSameEnvironments_200_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 1", response2.getStatus().size() == 1);
        assertThat("env name should be T1", response2.getStatus().get(0).getMiljo().equals("T1"));
        assertThat("status could should be 200", response2.getStatus().get(0).getResponse().getHttpStatus().getStatus().intValue() == 200);
    }

    @Test
    public void testMergePensjonforvalterResponses_withSameEnvironments_200_til_500() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 1", response2.getStatus().size() == 1);
        assertThat("env name should be T1", response2.getStatus().get(0).getMiljo().equals("T1"));
        assertThat("status could should be 500", response2.getStatus().get(0).getResponse().getHttpStatus().getStatus().intValue() == 500);
    }

    @Test
    public void testMergePensjonforvalterResponses_withSameEnvironments_500_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 1", response2.getStatus().size() == 1);
        assertThat("env name should be T1", response2.getStatus().get(0).getMiljo().equals("T1"));
        assertThat("status could should be 500", response2.getStatus().get(0).getResponse().getHttpStatus().getStatus().intValue() == 500);
    }

    @Test
    public void testMergePensjonforvalterResponses_withSameEnvironments_500_til_500() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 1", response2.getStatus().size() == 1);
        assertThat("env name should be T1", response2.getStatus().get(0).getMiljo().equals("T1"));
        assertThat("status could should be 500", response2.getStatus().get(0).getResponse().getHttpStatus().getStatus().intValue() == 500);
    }

    // none empty new response list to none empty previous list with different env name
    @Test
    public void testMergePensjonforvalterResponses_withDifferentEnvironments() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T2");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 2", response2.getStatus().size() == 2);
        assertThat("first env name should be T2", response2.getStatus().get(0).getMiljo().equals("T2"));
        assertThat("second env name should be T1", response2.getStatus().get(1).getMiljo().equals("T1"));
    }

    // none empty new reponse list with 2 env (status 200), previous list with 2 env but 1 is same, 1 is different (status 200)
    // none empty new reponse list with 2 env (status 200), previous list with 2 env but 1 is same, 1 is different (status 500)
    // none empty new reponse list with 2 env (status 500), previous list with 2 env but 1 is same, 1 is different (status 200)
    // none empty new reponse list with 2 env (status 500), previous list with 2 env but 1 is same, 1 is different (status 500)
    @Test
    public void testMergePensjonforvalterResponses_withMixedEnvironments_200_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T3", "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 3", response2.getStatus().size() == 3);
        assertThat("first env name should be T3", response2.getStatus().get(0).getMiljo().equals("T3"));
        assertThat("second env name should be T1", response2.getStatus().get(1).getMiljo().equals("T1"));
        assertThat("third env name should be T2", response2.getStatus().get(2).getMiljo().equals("T2"));
    }

    @Test
    public void testMergePensjonforvalterResponses_withMixedEnvironments_200_til_500() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T3", "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 3", response2.getStatus().size() == 3);
        assertThat("first env name should be T3", response2.getStatus().get(0).getMiljo().equals("T3"));
        assertThat("second env name should be T1", response2.getStatus().get(1).getMiljo().equals("T1"));
        assertThat("second env http status should be 500", response2.getStatus().get(1).getResponse().getHttpStatus().getStatus().intValue() == 500);
        assertThat("third env name should be T2", response2.getStatus().get(2).getMiljo().equals("T2"));
    }

    @Test
    public void testMergePensjonforvalterResponses_withMixedEnvironments_500_til_200() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 200, "T3", "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 3", response2.getStatus().size() == 3);
        assertThat("first env name should be T3", response2.getStatus().get(0).getMiljo().equals("T3"));
        assertThat("second env name should be T1", response2.getStatus().get(1).getMiljo().equals("T1"));
        assertThat("second env http status should be 500", response2.getStatus().get(1).getResponse().getHttpStatus().getStatus().intValue() == 500);
        assertThat("third env name should be T2", response2.getStatus().get(2).getMiljo().equals("T2"));
        assertThat("third env http status should be 500", response2.getStatus().get(2).getResponse().getHttpStatus().getStatus().intValue() == 500);
    }

    @Test
    public void testMergePensjonforvalterResponses_withMixedEnvironments_500_til_500() {
        var response1 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T1", "T2");
        var response2 = PensjonforvalterClientTestUtil.getPensjonforvalterResponse("ok", 500, "T3", "T1");

        mergePensjonforvalterResponses(response1, response2);

        assertThat("size for response2 should be 3", response2.getStatus().size() == 3);
        assertThat("first env name should be T3", response2.getStatus().get(0).getMiljo().equals("T3"));
        assertThat("first env http status should be 500", response2.getStatus().get(0).getResponse().getHttpStatus().getStatus().intValue() == 500);
        assertThat("second env name should be T1", response2.getStatus().get(1).getMiljo().equals("T1"));
        assertThat("second env http status should be 500", response2.getStatus().get(1).getResponse().getHttpStatus().getStatus().intValue() == 500);
        assertThat("third env name should be T2", response2.getStatus().get(2).getMiljo().equals("T2"));
        assertThat("third env http status should be 500", response2.getStatus().get(2).getResponse().getHttpStatus().getStatus().intValue() == 500);
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

        Person person = new Person();
        DollyPerson dollyPerson = new DollyPerson();
        dollyPerson.setHovedperson("000");
        dollyPerson.setPersondetaljer(List.of(person));

        BestillingProgress progress = new BestillingProgress();

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Set.of("TEST1", "TEST2"));

        var test1EnvResponse = new PensjonforvalterResponse.Response();
        test1EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));
        var test2EnvResponse = new PensjonforvalterResponse.Response();
        test2EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));

        PensjonforvalterResponse lagreTpForholdResponse = new PensjonforvalterResponse();
        lagreTpForholdResponse.setStatus(List.of(
                new PensjonforvalterResponse.ResponseEnvironment("TEST1", test1EnvResponse),
                new PensjonforvalterResponse.ResponseEnvironment("TEST2", test2EnvResponse)
        ));

        when(pensjonforvalterConsumer.lagreTpForhold(any(LagreTpForholdRequest.class)))
                .thenReturn(lagreTpForholdResponse);

        PensjonforvalterResponse lagreTpYtelseResponse = new PensjonforvalterResponse();
        lagreTpYtelseResponse.setStatus(List.of(
                new PensjonforvalterResponse.ResponseEnvironment("TEST1", test1EnvResponse),
                new PensjonforvalterResponse.ResponseEnvironment("TEST2", test2EnvResponse)
        ));
        when(pensjonforvalterConsumer.lagreTpYtelse(any(LagreTpYtelseRequest.class)))
                .thenReturn(lagreTpYtelseResponse);

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(LagreTpForholdRequest.class))).thenReturn(new LagreTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(LagreTpYtelseRequest.class))).thenReturn(new LagreTpYtelseRequest());

        pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false);

        assertThat("progress not null", progress != null);
        assertThat("TpForhold are Ok for both environment", progress.getPensjonforvalterStatus().contains("TpForhold#TEST1:OK,TEST2:OK"));
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

        Person person = new Person();
        DollyPerson dollyPerson = new DollyPerson();
        dollyPerson.setHovedperson("000");
        dollyPerson.setPersondetaljer(List.of(person));

        BestillingProgress progress = new BestillingProgress();

        when(pensjonforvalterConsumer.getMiljoer()).thenReturn(Set.of("TEST1", "TEST2"));

        var test1EnvResponse = new PensjonforvalterResponse.Response();
        test1EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));
        var test2EnvResponse = new PensjonforvalterResponse.Response();
        test2EnvResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 200));

        PensjonforvalterResponse lagreTpForholdResponse = new PensjonforvalterResponse();
        lagreTpForholdResponse.setStatus(List.of(
                new PensjonforvalterResponse.ResponseEnvironment("TEST1", test1EnvResponse),
                new PensjonforvalterResponse.ResponseEnvironment("TEST2", test2EnvResponse)
        ));

        when(pensjonforvalterConsumer.lagreTpForhold(any(LagreTpForholdRequest.class)))
                .thenReturn(lagreTpForholdResponse);

        var test2EnvYtelseResponse = new PensjonforvalterResponse.Response();
        test2EnvYtelseResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus("", 500));
        test2EnvYtelseResponse.setMessage("ytelse2 feil on TEST2");

        PensjonforvalterResponse lagreTpYtelseResponse = new PensjonforvalterResponse();
        lagreTpYtelseResponse.setStatus(List.of(
                new PensjonforvalterResponse.ResponseEnvironment("TEST1", test1EnvResponse),
                new PensjonforvalterResponse.ResponseEnvironment("TEST2", test2EnvYtelseResponse)
        ));
        when(pensjonforvalterConsumer.lagreTpYtelse(any(LagreTpYtelseRequest.class)))
                .thenReturn(lagreTpYtelseResponse);

        when(mapperFacade.map(any(PensjonData.TpOrdning.class), eq(LagreTpForholdRequest.class))).thenReturn(new LagreTpForholdRequest());
        when(mapperFacade.map(any(PensjonData.TpYtelse.class), eq(LagreTpYtelseRequest.class))).thenReturn(new LagreTpYtelseRequest());

        pensjonforvalterClient.gjenopprett(bestilling, dollyPerson, progress, false);

        assertThat("progress not null", progress != null);
        assertThat("TpForhold have Feil for TEST2 environment", progress.getPensjonforvalterStatus().contains("TpForhold#TEST1:OK,TEST2:Feil= ytelse2 feil on TEST2"));
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

        public static PensjonforvalterResponse getPensjonforvalterResponse(String httpStatusPhrase, int httpStatusCode, String... miljoe) {
            var response = new PensjonforvalterResponse();
            var status = new ArrayList<PensjonforvalterResponse.ResponseEnvironment>();

            var statusResponse = new PensjonforvalterResponse.Response();
            statusResponse.setHttpStatus(new PensjonforvalterResponse.HttpStatus(httpStatusPhrase, httpStatusCode));

            Arrays.stream(miljoe).forEach( m -> status.add(new PensjonforvalterResponse.ResponseEnvironment(m, statusResponse)) );

            response.setStatus(status);

            return response;
        }
    }
}
