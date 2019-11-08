package no.nav.dolly.appservices.tpsf.restcom;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.IdentType.FNR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
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

    private MockRestServiceServer server;

    @Autowired
    private RestTemplate restTemplate;

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
                        .url("https://localhost:8080")
                        .build());
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void opprettPersonerTpsf_hvisSuksessfultKallReturnerListeAvStringIdenter() throws Exception {
        standardTpsfBestilling.setIdenttype(FNR);
        ResponseEntity<Object> ob = new ResponseEntity<>("body", HttpStatus.OK);

        server.expect(requestTo("https://localhost:8080/api/v1/dolly/testdata/personer"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(standardTpsfBestilling)))
                .andRespond(withSuccess(asJsonString(ob), MediaType.APPLICATION_JSON));

        when(objectMapper.convertValue(anyMap(), eq(List.class))).thenReturn(singletonList(FNR));

        List<String> response = tpsfService.opprettIdenterTpsf(standardTpsfBestilling);

        assertThat(response.get(0), is(FNR));
    }

    @Test(expected = TpsfException.class)
    public void opprettPersonerTpsf_hvisTpsfKasterExceptionSaaKastesTpsfException() throws Exception{

        RestTemplateFailure failure = RestTemplateFailure.builder().error("tekst").status("feil").message("melding").build();

        server.expect(requestTo("https://localhost:8080/api/v1/dolly/testdata/personer"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(standardTpsfBestilling)))
                .andRespond(withServerError());

        when(objectMapper.readValue(any(byte[].class), eq(RestTemplateFailure.class))).thenReturn(failure);

        tpsfService.opprettIdenterTpsf(standardTpsfBestilling);
    }

    @Test(expected = TpsfException.class)
    public void sendIdenterTilTpsFraTPSF_hvisTpsfKasterExceptionSaaKastesTpsfException() throws Exception {

        RestTemplateFailure failure = RestTemplateFailure.builder().error("tekst").status("feil").message("melding").build();

        server.expect(requestTo("https://localhost:8080/api/v1/dolly/testdata/tilTpsFlere"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(new TpsfIdenterMiljoer(standardIdenter, standardMiljoer_u1_t1))))
                .andRespond(withServerError());

        when(objectMapper.readValue(any(byte[].class), eq(RestTemplateFailure.class))).thenReturn(failure);

        tpsfService.sendIdenterTilTpsFraTPSF(standardIdenter, standardMiljoer_u1_t1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendIdenterTilTpsFraTPSF_hvisIngenMiljoerErSpesifisertSaaKastesIllegalArgumentException() {
        List<String> tomListe = new ArrayList<>();
        tpsfService.sendIdenterTilTpsFraTPSF(standardIdenter, tomListe);
    }

    @Test
    public void sendTilTpsFraTPSF_happyPath() throws Exception {

        ResponseEntity<Object> ob = new ResponseEntity<>("{\"gruppeid\":\"1\"}", HttpStatus.OK);
        RsSkdMeldingResponse meldingResponse = RsSkdMeldingResponse.builder().gruppeid(1L).build();

        server.expect(requestTo("https://localhost:8080/api/v1/dolly/testdata/tilTpsFlere"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(asJsonString(new TpsfIdenterMiljoer(standardIdenter, standardMiljoer_u1_t1))))
                .andRespond(withSuccess(asJsonString(ob), MediaType.APPLICATION_JSON));

        when(objectMapper.convertValue(anyMap(), eq(RsSkdMeldingResponse.class))).thenReturn(meldingResponse);

        RsSkdMeldingResponse response = tpsfService.sendIdenterTilTpsFraTPSF(standardIdenter, standardMiljoer_u1_t1);

        assertThat(response, is(meldingResponse));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}