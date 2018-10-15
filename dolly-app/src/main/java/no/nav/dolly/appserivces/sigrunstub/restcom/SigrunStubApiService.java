package no.nav.dolly.appserivces.sigrunstub.restcom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.dolly.appserivces.tpsf.errorhandling.RestTemplateFailure;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.exceptions.SigrunnStubException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Service
public class SigrunStubApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = "/testdata/opprett";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProvidersProps providersProps;

    public ResponseEntity<String> createSkattegrunnlag(RsSigrunnOpprettSkattegrunnlag request) {
        StringBuilder sbUrl = new StringBuilder().append(providersProps.getSigrun().getUrl()).append(SIGRUN_STUB_OPPRETT_GRUNNLAG);
        try {
            OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
            String token = auth.getIdToken();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "Bearer " + token);
            header.set("testdataEier", auth.getPrincipal());
            HttpEntity entity = new HttpEntity(request, header);
            ResponseEntity<String> response = restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, entity, String.class);
            return response;
        } catch (HttpClientErrorException e) {
            RestTemplateFailure rs = lesOgMapFeilmelding(e);
            throw new SigrunnStubException("Sigrun-Stub kall feilet med: " + rs.getMessage());
        }
    }

    private RestTemplateFailure lesOgMapFeilmelding(HttpClientErrorException e) {
        RestTemplateFailure failure = new RestTemplateFailure();
        failure.setMessage(e.getResponseBodyAsString());
        return failure;
    }
}