package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;

@Slf4j
@Component
public class RettighetArenaForvalterConsumer {

    private final RestTemplate restTemplate;

    private String arenaForvalterServerUrl;

    public RettighetArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.arenaForvalterServerUrl = arenaForvalterServerUrl;
    }

    public Map<String, List<NyttVedtakResponse>> opprettRettighet(List<RettighetRequest> rettigheter) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        for (var rettighet : rettigheter) {
            var url = new UriTemplate(arenaForvalterServerUrl + rettighet.getArenaForvalterUrlPath());

            var postRequest = RequestEntity.post(url.expand())
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .body(rettighet);
            NyttVedtakResponse response = null;
            try {
                response = restTemplate.exchange(postRequest, NyttVedtakResponse.class).getBody();
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke opprette rettighet i arena-forvalteren.", e);
            }
            if (response != null) {
                if (responses.containsKey(rettighet.getPersonident())) {
                    responses.get(rettighet.getPersonident()).add(response);
                } else {
                    responses.put(rettighet.getPersonident(), new ArrayList<>(Collections.singletonList(response)));
                }
            }
        }
        return responses;
    }
}
