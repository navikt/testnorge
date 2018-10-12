package no.nav.dolly.appserivces.sigrunstub.restcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsGrunnlagResponse;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.appserivces.tpsf.errorhandling.RestTemplateFailure;
import no.nav.dolly.exceptions.SigrunnStubException;
import no.nav.dolly.properties.ProvidersProps;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SigrunStubApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = "/testdata/opprett";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProvidersProps providersProps;

    public List<RsGrunnlagResponse> createInntektstuff(RsSigrunnOpprettSkattegrunnlag request) {
        StringBuilder sbUrl = new StringBuilder().append(providersProps.getSigrun()).append(SIGRUN_STUB_OPPRETT_GRUNNLAG);
        try{
            ResponseEntity<RsGrunnlagResponse[]> response = restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, new HttpEntity<>(request), RsGrunnlagResponse[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException|HttpServerErrorException e) {
            RestTemplateFailure rs = lesOgMapFeilmelding(e);
            log.error("Sigrun-Stub kall feilet mot url <{}> grunnet {}", sbUrl.toString(),  rs.getMessage());
            throw new SigrunnStubException("Sigrun-Stub kall feilet med: " + rs.getMessage());
        }
    }

    private RestTemplateFailure lesOgMapFeilmelding(HttpStatusCodeException e){
        try {
            return objectMapper.readValue(e.getResponseBodyAsString(), RestTemplateFailure.class);
        } catch (IOException ex){
            throw e;
        }
    }
}
