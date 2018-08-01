package no.nav.appserivces.sigrunstub.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.resultSet.RsGrunnlagResponse;
import no.nav.resultSet.RsSigrunnOpprettSkattegrunnlag;
import no.nav.appserivces.tpsf.errorHandling.RestTemplateException;
import no.nav.exceptions.SigrunnStubException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SigrunStubApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = "/testdata/opprett";

    @Autowired
    ObjectMapper objectMapper;

    @Value("${sigrun.server.url}")
    private String sigrunHostUrl;

    public List<RsGrunnlagResponse> createInntektstuff(RsSigrunnOpprettSkattegrunnlag request) throws IOException, SigrunnStubException{
        StringBuilder sbUrl = new StringBuilder().append(sigrunHostUrl).append(SIGRUN_STUB_OPPRETT_GRUNNLAG);

        try{
            ResponseEntity<RsGrunnlagResponse[]> response = restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, new HttpEntity<>(request), RsGrunnlagResponse[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException e){
            RestTemplateException rs = objectMapper.readValue(e.getResponseBodyAsString(), RestTemplateException.class);
            throw new SigrunnStubException("Sigrun-Stub kall feilet med: " + rs.getMessage());
        } catch (HttpServerErrorException e){
            RestTemplateException rs = objectMapper.readValue(e.getResponseBodyAsString(), RestTemplateException.class);
            throw new SigrunnStubException("Sigrun-Stub kall feilet med: " + rs.getMessage());
        }

    }
}
