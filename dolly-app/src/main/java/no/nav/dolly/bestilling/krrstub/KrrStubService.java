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
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Service
public class KrrStubService {

    private static final String KRR_STUB_DIGITAL_KONTAKT = "/api/v1/kontaktinformasjon";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ProvidersProps providersProps;

    public ResponseEntity<Object> createDigitalKontaktdata(Long bestillingsid, DigitalKontaktdataRequest digitalKontaktdata) {

        String url = format("%s%s", providersProps.getKrrStub().getUrl(), KRR_STUB_DIGITAL_KONTAKT);

        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("Nav-Call-Id", Long.toString(bestillingsid));
        header.add("Nav-Consumer-Id", auth.getPrincipal());

        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(digitalKontaktdata, header), Object.class);
    }
}