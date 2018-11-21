package no.nav.dolly.bestilling.sigrunstub;

import static java.lang.String.format;

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
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Slf4j
@Service
public class SigrunStubService {

    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = "/testdata/opprettBolk";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProvidersProps providersProps;

    public ResponseEntity<String> createSkattegrunnlag(List<RsOpprettSkattegrunnlag> request) {

        String url = format("%s%s", providersProps.getSigrunStub().getUrl(), SIGRUN_STUB_OPPRETT_GRUNNLAG);
        try {
            OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
            String token = auth.getIdToken();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "Bearer " + token);
            header.set("testdataEier", auth.getPrincipal());

            return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(request, header), String.class);

        } catch (HttpClientErrorException e) {
            log.error("SigrunStub kall feilet mot url <{}> grunnet {}", url, e.getResponseBodyAsString(), e);
            return new ResponseEntity(e.getStatusCode());
        }
    }
}