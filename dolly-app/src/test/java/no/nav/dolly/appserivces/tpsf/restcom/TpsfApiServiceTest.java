package no.nav.dolly.appserivces.tpsf.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.appserivces.tpsf.errorhandling.RestTemplateFailure;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.exceptions.TpsfException;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TpsfApiServiceTest {

    private String url = "https://localhost:8080/api/v1/dolly/testdata/personer";

    @Mock
    RestTemplate restTemplate;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    private TpsfApiService service;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(service, "tpsfServerUrl", "https://localhost:8080");
    }

    @Test
    public void opprettPersonerTpsf_hvisSuksessfultKallReturnerListeAvStringIdenter(){
//        RsDollyBestillingsRequest req = new RsDollyBestillingsRequest();
//        req.setIdenttype("FNR");
//
//        Object s = "body";
//        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
//
//        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
//        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
//        when(objectMapper.convertValue("body", List.class)).thenReturn(Arrays.asList("test"));
//
//        List<String> res = service.opprettIdenterTpsf(req);
//        verify(restTemplate).exchange(endpointCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));
//
//        HttpEntity entity = httpEntityCaptor.getValue();
//
//        assertThat(res.get(0), is("test"));
//        assertThat(endpointCaptor.getValue(), is(url));
//        assertThat(entity.getBody(), is(req));
//        assertThat((entity.getBody()), is(req));
    }

//    @Test(expected = TpsfException.class)
    @Test
    public void opprettPersonerTpsf_hvisTpsfKasterExceptionSaaKastesTpsfException(){
//        RsDollyBestillingsRequest req = new RsDollyBestillingsRequest();
//
//        Object s = "exception=Feil";
//        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
//        RestTemplateFailure resExp = new RestTemplateFailure();
//        resExp.setMessage("msg");
//        resExp.setError("err");
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
//        when(objectMapper.convertValue(s, RestTemplateFailure.class)).thenReturn(resExp);
//
//        service.opprettIdenterTpsf(req);
    }

    @Test
    public void sendTilTpsFraTPSF_happyPath(){
//        String ident = "123";
//        List<String> miljoer = Arrays.asList("u1", "t1");
//
//        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
//        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
//
//        Object s = "body";
//        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
//
//        RsSkdMeldingResponse res = new RsSkdMeldingResponse();
//
//        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
//        when(objectMapper.convertValue(s, RsSkdMeldingResponse.class)).thenReturn(res);
//
//        RsSkdMeldingResponse ressponse = service.sendTilTpsFraTPSF(ident, miljoer);
//        verify(restTemplate).exchange(endpointCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));
//
//        assertThat(endpointCaptor.getValue().contains("u1"), is(true));
//        assertThat(endpointCaptor.getValue().contains("t1"), is(true));
//        assertThat(ressponse, is(res));
    }
}