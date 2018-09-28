package no.nav.dolly.appserivces.sigrunstub.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.appserivces.tpsf.errorhandling.RestTemplateFailure;
import no.nav.dolly.domain.resultset.RsGrunnlagResponse;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.exceptions.SigrunnStubException;
import no.nav.dolly.properties.ProvidersProps;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SigrunStubApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    ObjectMapper objectMapper;

    @Mock ProvidersProps providersProps;

    @InjectMocks
    private SigrunStubApiService service;

    @Before
    public void setup(){
        when(providersProps.getSigrun().getUrl()).thenReturn("https://localhost:8080");
    }

    @Test
    public void createInntektstuff() {
        RsGrunnlagResponse[] gListe = new RsGrunnlagResponse[1];
        RsGrunnlagResponse grunn = new RsGrunnlagResponse();
        grunn.setGrunnlag("grunnlag");
        gListe[0] = grunn;

        ResponseEntity<RsGrunnlagResponse[]> res = new ResponseEntity<>(gListe, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(RsGrunnlagResponse[].class))).thenReturn(res);

        List<RsGrunnlagResponse> response = service.createInntektstuff(new RsSigrunnOpprettSkattegrunnlag());

        assertThat(response.get(0).getGrunnlag(), is("grunnlag"));
    }

    @Test(expected = SigrunnStubException.class)
    public void createInntektstuff_kasterSigrunExceptionHvisKallKasterClientException() throws Exception{
        RsGrunnlagResponse[] gListe = new RsGrunnlagResponse[1];
        RsGrunnlagResponse grunn = new RsGrunnlagResponse();
        grunn.setGrunnlag("grunnlag");
        gListe[0] = grunn;

        ResponseEntity<RsGrunnlagResponse[]> res = new ResponseEntity<>(gListe, HttpStatus.OK);
        RestTemplateFailure ex = new RestTemplateFailure();
        ex.setMessage("msg");

        HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.OK, "OK");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(RsGrunnlagResponse[].class))).thenThrow(clientErrorException);
        when(objectMapper.readValue(anyString(), eq(RestTemplateFailure.class))).thenReturn(ex);

        service.createInntektstuff(new RsSigrunnOpprettSkattegrunnlag());
    }

    @Test(expected = SigrunnStubException.class)
    public void createInntektstuff_kasterSigrunExceptionHvisKallKasterServerError() throws Exception{
        RsGrunnlagResponse[] gListe = new RsGrunnlagResponse[1];
        RsGrunnlagResponse grunn = new RsGrunnlagResponse();
        grunn.setGrunnlag("grunnlag");
        gListe[0] = grunn;

        ResponseEntity<RsGrunnlagResponse[]> res = new ResponseEntity<>(gListe, HttpStatus.OK);
        RestTemplateFailure ex = new RestTemplateFailure();
        ex.setMessage("msg");

        HttpServerErrorException clientErrorException = new HttpServerErrorException(HttpStatus.OK, "OK");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(RsGrunnlagResponse[].class))).thenThrow(clientErrorException);
        when(objectMapper.readValue(anyString(), eq(RestTemplateFailure.class))).thenReturn(ex);

        service.createInntektstuff(new RsSigrunnOpprettSkattegrunnlag());
    }
}