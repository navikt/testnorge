package no.nav.dolly.kodeverk;

import static no.nav.dolly.util.CallIdUtil.generateCallId;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.KodeverkException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KodeverkConsumer {

    private static final String APP_BRUKERNAVN = "srvdolly";
    private static final String HEADER_NAME_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String HEADER_NAME_CALL_ID = "Nav-Call-id";
    private static final String EMPTY_BODY = "empty";
    private static final String KODEVERK_URL_QUERY_PARAMS_EKSKLUDER_UGYLDIGE_SPRAAK_NB = "?ekskluderUgyldige=true&spraak=nb";
    private static final String KODEVERK_URL_BASE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public GetKodeverkKoderBetydningerResponse fetchKodeverkByName(String navn) {
        String url = providersProps.getKodeverk().getUrl() + getKodeverksnavnUrl(navn) + KODEVERK_URL_QUERY_PARAMS_EKSKLUDER_UGYLDIGE_SPRAAK_NB;
        HttpEntity entity = buildKodeverkEntityForGET();

        try {
            ResponseEntity<GetKodeverkKoderBetydningerResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, GetKodeverkKoderBetydningerResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new KodeverkException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    private HttpEntity buildKodeverkEntityForGET() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME_CONSUMER_ID, APP_BRUKERNAVN);
        headers.set(HEADER_NAME_CALL_ID, generateCallId());
        return new HttpEntity(EMPTY_BODY, headers);
    }

    private String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_BASE.replace("{kodeverksnavn}", kodeverksnavn);
    }
}
