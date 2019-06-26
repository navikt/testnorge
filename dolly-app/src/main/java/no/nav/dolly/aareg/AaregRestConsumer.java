package no.nav.dolly.aareg;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.sts.StsOidcService;

@Service
public class AaregRestConsumer {

    @Autowired
    private AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StsOidcService stsOidcService;

    public ResponseEntity readArbeidsforhold(String ident, String environment) {

        return restTemplate.exchange(RequestEntity
                        .get(URI.create(aaregArbeidsforholdFasitConsumer.getUrlForEnv(environment)))
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, stsOidcService.getIdToken(environment))
                        .header("Nav-Consumer-Token", stsOidcService.getIdToken(environment))
                        .header("Nav-Personident", ident)
                        .build(),
                Object[].class);
    }
}
