package no.nav.dolly.bestilling.krrstub;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.exceptions.KrrStubException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Slf4j
@Service
public class KrrStubApiService {

    private static final String KRR_STUB_DIGITAL_KONTAKT = "/api/v1/kontaktinformasjon";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProvidersProps providersProps;

    public ResponseEntity<String> createDigitalKontaktdata(Long bestillingsid, RsDigitalKontaktdata digitalKontaktdata) {

        String url = format("%s%s", providersProps.getKrrStub().getUrl(), KRR_STUB_DIGITAL_KONTAKT);
        try {
            OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            header.add("Nav-Call-Id", Long.toString(bestillingsid));
            header.add("Nav-Consumer-Id", auth.getPrincipal());

            return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(digitalKontaktdata, header), String.class);

        } catch (HttpClientErrorException e) {
            log.error("KrrStub kall feilet mot url <{}> grunnet {}", url, e.getResponseBodyAsString());
            throw new KrrStubException("KrrStub kall feilet med: " + e, e);
        }
    }
}