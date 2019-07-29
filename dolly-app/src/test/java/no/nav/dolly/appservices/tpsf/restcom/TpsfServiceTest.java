package no.nav.dolly.appservices.tpsf.restcom;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.IdentType.FNR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
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
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.dolly.bestilling.errorhandling.RestTemplateFailure;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.TpsfIdenterMiljoer;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class TpsfServiceTest {

    private static final String url = "https://localhost:8080/api/v1/dolly/testdata/personer";
    private static final TpsfBestilling standardTpsfBestilling = new TpsfBestilling();
    private static final String standardIdent = "123";
    private static final List<String> standardIdenter = new ArrayList<>(singleton(standardIdent));
    private static final List<String> standardMiljoer_u1_t1 = Arrays.asList("u1", "t1");

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private TpsfService service;

    @Before
    public void setup(){
        ProvidersProps.Tpsf tpsf = new ProvidersProps.Tpsf();
        tpsf.setUrl("https://localhost:8080");
        when(providersProps.getTpsf()).thenReturn(tpsf);
    }

    @Test
    public void opprettPersonerTpsf_hvisSuksessfultKallReturnerListeAvStringIdenter(){
        standardTpsfBestilling.setIdenttype(FNR);

        Object s = "body";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);

        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue("body", List.class)).thenReturn(singletonList("test"));

        List<String> res = service.opprettIdenterTpsf(standardTpsfBestilling);
        verify(restTemplate).exchange(endpointCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        HttpEntity entity = httpEntityCaptor.getValue();

        assertThat(res.get(0), is("test"));
        assertThat(endpointCaptor.getValue(), is(url));
        assertThat(entity.getBody(), is(standardTpsfBestilling));
        assertThat((entity.getBody()), is(standardTpsfBestilling));
    }

    @Test(expected = TpsfException.class)
    public void opprettPersonerTpsf_hvisTpsfKasterExceptionSaaKastesTpsfException(){
        Object s = "error=Feil";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
        RestTemplateFailure resExp = new RestTemplateFailure();
        resExp.setMessage("msg");
        resExp.setError("err");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue(s, RestTemplateFailure.class)).thenReturn(resExp);

        service.opprettIdenterTpsf(standardTpsfBestilling);
    }

    @Test(expected = TpsfException.class)
    public void sendIdenterTilTpsFraTPSF_hvisTpsfKasterExceptionSaaKastesTpsfException(){
        Object s = "error=Feil";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
        RestTemplateFailure resExp = new RestTemplateFailure();
        resExp.setMessage("msg");
        resExp.setError("err");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue(s, RestTemplateFailure.class)).thenReturn(resExp);

        service.sendIdenterTilTpsFraTPSF(standardIdenter, standardMiljoer_u1_t1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendIdenterTilTpsFraTPSF_hvisIngenMiljoerErSpesifisertSaaKastesIllegalArgumentException(){
        List<String> tomListe = new ArrayList<>();
        service.sendIdenterTilTpsFraTPSF(standardIdenter, tomListe);
    }

    @Test
    public void sendTilTpsFraTPSF_happyPath(){
        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        Object s = "body";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
        RsSkdMeldingResponse res = new RsSkdMeldingResponse();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue(s, RsSkdMeldingResponse.class)).thenReturn(res);

        RsSkdMeldingResponse ressponse = service.sendIdenterTilTpsFraTPSF(standardIdenter, standardMiljoer_u1_t1);
        verify(restTemplate).exchange(endpointCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        assertThat(((TpsfIdenterMiljoer) httpEntityCaptor.getValue().getBody()).getMiljoer(), Matchers.containsInAnyOrder("u1", "t1"));
        assertThat(ressponse, is(res));
    }
}