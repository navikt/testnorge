package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.consumer.rs.ConsumerUtils.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.ConsumerUtils.NAV_CONSUMER_ID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@Slf4j
@Component
public class RettighetTilleggArenaForvalterConsumer {

    private final RestTemplate restTemplate;

    private UriTemplate opprettTilleggsstoenadUrl;

    public RettighetTilleggArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettTilleggsstoenadUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/tilleggsstonad");
    }

    public List<NyttVedtakResponse> opprettRettighet(List<RettighetRequest> rettigheter) {
        List<NyttVedtakResponse> responses = new ArrayList<>(rettigheter.size());
        for (var rettighet : rettigheter) {
            UriTemplate url;
            if (rettighet instanceof RettighetTilleggRequest) {
                url = opprettTilleggsstoenadUrl;
            } else {
                throw new RuntimeException("Unkown URL");
            }

            var postRequest = RequestEntity.post(url.expand())
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .body(rettighet);
            responses.add(restTemplate.exchange(postRequest, NyttVedtakResponse.class).getBody());
        }

        return responses;
    }
}
