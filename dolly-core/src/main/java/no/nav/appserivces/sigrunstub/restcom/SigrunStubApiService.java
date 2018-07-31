package no.nav.appserivces.sigrunstub.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.appserivces.sigrunstub.domain.RsSigrunnOpprettSkattegrunnlag;
import no.nav.appserivces.tpsf.errorHandling.RestTemplateException;
import no.nav.exceptions.SigrunnStubException;
import no.nav.exceptions.TpsfException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SigrunStubApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String SIGRUN_STUB_BASE_URL = "http://localhost:8040";
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = "/testdata/opprett";

    @Autowired
    ObjectMapper objectMapper;

    public void createInntektstuff(RsSigrunnOpprettSkattegrunnlag request) throws IOException, SigrunnStubException{
        StringBuilder sbUrl = new StringBuilder().append(SIGRUN_STUB_BASE_URL).append(SIGRUN_STUB_OPPRETT_GRUNNLAG);

        HttpHeaders header = new HttpHeaders();
        header.add("testdataEier", "Dolly");
        HttpEntity<Object> req = new HttpEntity<>(request, header);

        ResponseEntity<Object> response =null;

        try{
            response = restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, req, Object.class);
        } catch (HttpClientErrorException e){
            RestTemplateException rs = objectMapper.readValue(e.getResponseBodyAsString(), RestTemplateException.class);
        } catch (HttpServerErrorException e){
            RestTemplateException rs = objectMapper.readValue(e.getResponseBodyAsString(), RestTemplateException.class);
            throw new SigrunnStubException("Sigrun-Stub kall feilet med: " + rs.getMessage());
        }

    }
}
