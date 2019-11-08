package no.nav.dolly.appservices.tpsf.restcom;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.IdentType.FNR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.dolly.bestilling.errorhandling.RestTemplateFailure;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfIdenterMiljoer;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.properties.ProvidersProps;

@Ignore
@RunWith(SpringRunner.class)
@RestClientTest(TpsfService.class)
public class TpsfServiceTest {

    private static final String url = "https://localhost:8080/api/v1/dolly/testdata/personer";
    private static final TpsfBestilling standardTpsfBestilling = new TpsfBestilling();
    private static final String standardIdent = "123";
    private static final List<String> standardIdenter = new ArrayList<>(singleton(standardIdent));
    private static final List<String> standardMiljoer_u1_t1 = Arrays.asList("u1", "t1");

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private TpsfService tpsfService;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private ProvidersProps providersProps;

    @Before
    public void setup() {

        when(providersProps.getTpsf())
                .thenReturn(ProvidersProps.Tpsf.builder()
                        .url("baseurl")
                        .build());
    }

    @Test
    public void opprettPersonerTpsf_hvisSuksessfultKallReturnerListeAvStringIdenter() {
        standardTpsfBestilling.setIdenttype(FNR);

        Object s = "body";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);

        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        //        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue("body", List.class)).thenReturn(singletonList("test"));

        List<String> res = tpsfService.opprettIdenterTpsf(standardTpsfBestilling);
        //        verify(restTemplate).exchange(endpointCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        HttpEntity entity = httpEntityCaptor.getValue();

        assertThat(res.get(0), is("test"));
        assertThat(endpointCaptor.getValue(), is(url));
        assertThat(entity.getBody(), is(standardTpsfBestilling));
        assertThat((entity.getBody()), is(standardTpsfBestilling));
    }

    @Test(expected = TpsfException.class)
    public void opprettPersonerTpsf_hvisTpsfKasterExceptionSaaKastesTpsfException() {
        Object s = "error=Feil";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
        RestTemplateFailure resExp = new RestTemplateFailure();
        resExp.setMessage("msg");
        resExp.setError("err");

        //        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue(s, RestTemplateFailure.class)).thenReturn(resExp);

        tpsfService.opprettIdenterTpsf(standardTpsfBestilling);
    }

    @Test(expected = TpsfException.class)
    public void sendIdenterTilTpsFraTPSF_hvisTpsfKasterExceptionSaaKastesTpsfException() {
        Object s = "error=Feil";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
        RestTemplateFailure resExp = new RestTemplateFailure();
        resExp.setMessage("msg");
        resExp.setError("err");

        //        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue(s, RestTemplateFailure.class)).thenReturn(resExp);

        tpsfService.sendIdenterTilTpsFraTPSF(standardIdenter, standardMiljoer_u1_t1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendIdenterTilTpsFraTPSF_hvisIngenMiljoerErSpesifisertSaaKastesIllegalArgumentException() {
        List<String> tomListe = new ArrayList<>();
        tpsfService.sendIdenterTilTpsFraTPSF(standardIdenter, tomListe);
    }

    @Test
    public void sendTilTpsFraTPSF_happyPath() throws Exception {
        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        Object s = "body";
        ResponseEntity<Object> ob = new ResponseEntity<>(s, HttpStatus.OK);
        RsSkdMeldingResponse res = new RsSkdMeldingResponse();

        //        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class))).thenReturn(ob);
        when(objectMapper.convertValue(s, RsSkdMeldingResponse.class)).thenReturn(res);

//        server.expect(requestTo("https://localhost:8080/api/v1/dolly/testdata/tilTpsFlere"))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(asJsonString(ob), MediaType.APPLICATION_JSON));

        RsSkdMeldingResponse response = tpsfService.sendIdenterTilTpsFraTPSF(standardIdenter, standardMiljoer_u1_t1);
        //        verify(restTemplate).exchange(endpointCaptor.capture(), httpMethodCaptor.capture(), httpEntityCaptor.capture(), eq(Object.class));

        assertThat(((TpsfIdenterMiljoer) httpEntityCaptor.getValue().getBody()).getMiljoer(), Matchers.containsInAnyOrder("u1", "t1"));
        assertThat(response, is(res));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}