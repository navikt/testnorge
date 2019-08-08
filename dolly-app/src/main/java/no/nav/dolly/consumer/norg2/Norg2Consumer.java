package no.nav.dolly.consumer.norg2;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class Norg2Consumer {

    private static final String APP_BRUKERNAVN = "srvdolly";
    private static final String EMPTY_BODY = "empty";
    private static final String NORG2_URL_BASE = "/api/v1/enhet/{enhetNr}";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public Norg2EnhetResponse fetchEnhetByEnhetNr(String enhetNr) {
        String url = providersProps.getNorg2().getUrl() + NORG2_URL_BASE.replace("{enhetNr}", enhetNr);
        HttpEntity entity = buildKodeverkEntityForGET();

        try {
            ResponseEntity<Norg2EnhetResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, Norg2EnhetResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new NotFoundException(format("Enhet med nummeret '%s' eksisterer ikke", enhetNr), e);
        }
    }

    private HttpEntity buildKodeverkEntityForGET() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAV_CONSUMER_ID, APP_BRUKERNAVN);
        headers.set(HEADER_NAV_CALL_ID, generateCallId());
        return new HttpEntity<>(EMPTY_BODY, headers);
    }
}
