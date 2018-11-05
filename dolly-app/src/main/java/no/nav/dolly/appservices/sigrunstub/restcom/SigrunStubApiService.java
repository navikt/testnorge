package no.nav.dolly.appservices.sigrunstub.restcom;

import java.util.List;
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

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.appservices.tpsf.errorhandling.RestTemplateFailure;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.exceptions.SigrunnStubException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Slf4j
@Service
public class SigrunStubApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = "/testdata/opprettBolk";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProvidersProps providersProps;

    public ResponseEntity<String> createSkattegrunnlag(List<RsSigrunnOpprettSkattegrunnlag> request) {

        StringBuilder sbUrl = new StringBuilder().append(providersProps.getSigrun().getUrl()).append(SIGRUN_STUB_OPPRETT_GRUNNLAG);
        try {
            OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
            String token = auth.getIdToken();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "Bearer " + token);
            header.set("testdataEier", auth.getPrincipal());
            HttpEntity entity = new HttpEntity(request, header);

            return restTemplate.exchange(sbUrl.toString(), HttpMethod.POST, entity, String.class);

        } catch (HttpClientErrorException e) {
            RestTemplateFailure rs = lesOgMapFeilmelding(e);
            log.error("Sigrun-Stub kall feilet mot url <{}> grunnet {}", sbUrl.toString(),  rs.getMessage());
            throw new SigrunnStubException("Sigrun-Stub kall feilet med: " + rs.getMessage());
        }
    }

    private RestTemplateFailure lesOgMapFeilmelding(HttpClientErrorException e) {
        RestTemplateFailure failure = new RestTemplateFailure();
        failure.setMessage(e.getResponseBodyAsString());
        return failure;
    }
}