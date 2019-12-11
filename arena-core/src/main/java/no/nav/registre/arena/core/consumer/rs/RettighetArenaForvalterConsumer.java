package no.nav.registre.arena.core.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer.UngUfoerForvalterResponse;

@Component
public class RettighetArenaForvalterConsumer {

    private static final String NAV_CALL_ID = "ORKESTRATOREN";
    private static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    private final RestTemplate restTemplate;

    private UriTemplate opprettUngUfoerRettighetUrl;

    public RettighetArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettUngUfoerRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapungufor");
    }

    public List<UngUfoerForvalterResponse> opprettRettighetUngUfoer(List<RettighetUngUfoerRequest> rettigheter) {
        List<UngUfoerForvalterResponse> responses = new ArrayList<>();
        for (RettighetUngUfoerRequest rettighet : rettigheter) {
            RequestEntity postRequest = RequestEntity.post(opprettUngUfoerRettighetUrl.expand())
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .body(rettighet);
            responses.add(restTemplate.exchange(postRequest, UngUfoerForvalterResponse.class).getBody());
        }
        return responses;
    }
}
