package no.nav.dolly.consumer.kodeverk;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.kodeverk.domain.GetKodeverkKoderBetydningerResponse;
import no.nav.dolly.exceptions.KodeverkException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Component
@RequiredArgsConstructor
public class KodeverkConsumer {

    private static final String APP_BRUKERNAVN = "srvdolly";
    private static final String EMPTY_BODY = "empty";
    private static final String QUERY_PARAMS = "?ekskluderUgyldige=true&spraak=nb";
    private static final String KODEVERK_URL_BASE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public GetKodeverkKoderBetydningerResponse fetchKodeverkByName(String navn) {
        String url = providersProps.getKodeverk().getUrl() + getKodeverksnavnUrl(navn) + QUERY_PARAMS;
        HttpEntity entity = buildKodeverkEntityForGET();

        try {
            ResponseEntity<GetKodeverkKoderBetydningerResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, GetKodeverkKoderBetydningerResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new KodeverkException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public Map<String, String> getKodeverkByName(String navn) {

        try {
            ResponseEntity<GetKodeverkKoderBetydningerResponse> response =
                    restTemplate.exchange(RequestEntity.get(
                            URI.create(providersProps.getKodeverk().getUrl() + getKodeverksnavnUrl(navn) + QUERY_PARAMS))
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .header(HEADER_NAV_CALL_ID, generateCallId())
                            .build(), GetKodeverkKoderBetydningerResponse.class);
            return response.getBody().getBetydninger().entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0).getBeskrivelser().get("nb").getTekst()));
        } catch (HttpClientErrorException e) {
            throw new KodeverkException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    private HttpEntity buildKodeverkEntityForGET() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAV_CONSUMER_ID, APP_BRUKERNAVN);
        headers.set(HEADER_NAV_CALL_ID, generateCallId());
        return new HttpEntity<>(EMPTY_BODY, headers);
    }

    private String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_BASE.replace("{kodeverksnavn}", kodeverksnavn);
    }
}
