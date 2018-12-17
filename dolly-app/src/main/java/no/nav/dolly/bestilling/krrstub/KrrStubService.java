package no.nav.dolly.bestilling.krrstub;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Slf4j
@Service
public class KrrStubService {

    private static final String KRR_STUB_DIGITAL_KONTAKT = "/api/v1/kontaktinformasjon";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<Object> createDigitalKontaktdata(Long bestillingsid, DigitalKontaktdataRequest digitalKontaktdata) {

        String url = format("%s%s", providersProps.getKrrStub().getUrl(), KRR_STUB_DIGITAL_KONTAKT);

        try {
            OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            header.add("Nav-Call-Id", Long.toString(bestillingsid));
            header.add("Nav-Consumer-Id", auth.getPrincipal());

            return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(digitalKontaktdata, header), Object.class);

        } catch (HttpClientErrorException e) {
            log.error("KrrStub kall feilet mot url <{}> grunnet {}", url, e.getResponseBodyAsString(), e);

            try {
                Envelope failure = objectMapper.readValue(e.getResponseBodyAsString().getBytes(UTF_8), Envelope.class);
                throw new TpsfException(format("%s -- (%s %s)", failure.getMelding(), e.getStatusCode(), e.getStatusText()), e);
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
            return new ResponseEntity(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    @Getter
    @Setter
    private static class Envelope {
        private String melding;
    }
}