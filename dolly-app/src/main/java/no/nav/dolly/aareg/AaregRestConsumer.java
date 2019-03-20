package no.nav.dolly.aareg;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Service
public class AaregRestConsumer {

    @Autowired
    private AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity readArbeidsforhold(String ident, String envionment) {

        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.add(AUTHORIZATION, "Bearer " + auth.getIdToken());
        headers.add("Nav-Consumer-Token", "Bearer" + auth.getIdToken());
        headers.add("Nav-Personident", ident);

        return restTemplate.exchange(
                aaregArbeidsforholdFasitConsumer.fetchRestUrlsAllEnvironments().get(envionment),
                HttpMethod.GET, new HttpEntity(headers), Object[].class);
    }
}
