package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArenaForvalterConsumer {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";
    private static final String KILDE = "Dolly";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    private static String getCallId() {
        return "Dolly: " + UUID.randomUUID().toString();
    }

    public ResponseEntity<ArenaArbeidssokerBruker> getIdent(String ident) {
        URI uri = URI.create(format("%s%s?filter-personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, ident));
        return doRequest(GET, uri, null, ArenaArbeidssokerBruker.class);
    }

    public void deleteIdent(String ident, String environment) {
        URI uri = URI.create(format("%s%s?miljoe=%s&personident=%s", providersProps.getArenaForvalter().getUrl(), ARENAFORVALTER_BRUKER, environment, ident));
        doRequest(DELETE, uri, null, JsonNode.class);
    }

    public ResponseEntity<ArenaArbeidssokerBruker> postArenadata(ArenaNyeBrukere arenaNyeBrukere) {
        URI uri = URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_BRUKER);
        return doRequest(POST, uri, arenaNyeBrukere, ArenaArbeidssokerBruker.class);
    }

    public List<String> getEnvironments() {
        ParameterizedTypeReference<List<String>> expectedResponseType = new ParameterizedTypeReference<List<String>>() {
        };
        ResponseEntity<List<String>> resp = restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getArenaForvalter().getUrl() + ARENAFORVALTER_ENVIRONMENTS))
                .header(HEADER_NAV_CALL_ID, getCallId())
                .header(HEADER_NAV_CONSUMER_ID, KILDE)
                .build(), expectedResponseType);
        return resp.getBody() != null ? resp.getBody() : emptyList();
    }

    private <T> ResponseEntity<T> doRequest(HttpMethod method, URI uri, ArenaNyeBrukere body, Class<T> clazz) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HEADER_NAV_CALL_ID, getCallId());
        httpHeaders.add(HEADER_NAV_CONSUMER_ID, KILDE);

        return restTemplate.exchange(uri, method, new HttpEntity<>(body, httpHeaders), clazz);
    }
}
