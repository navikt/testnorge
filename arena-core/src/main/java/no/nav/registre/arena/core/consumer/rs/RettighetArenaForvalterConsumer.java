package no.nav.registre.arena.core.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.ArenaForvalterNyRettighetResponse;

@Component
public class RettighetArenaForvalterConsumer {

    private static final String NAV_CALL_ID = "ORKESTRATOREN";
    private static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    private final RestTemplate restTemplate;

    private UriTemplate opprettUngUfoerRettighetUrl;
    private UriTemplate opprettTvungenForvaltningRettighetUrl;
    private UriTemplate opprettFritakMeldekortRettighetUrl;

    public RettighetArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettUngUfoerRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapungufor");
        this.opprettTvungenForvaltningRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aaptvungenforvaltning");
        this.opprettFritakMeldekortRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapfritakmeldekort");
    }

    public List<ArenaForvalterNyRettighetResponse> opprettRettighet(List<RettighetRequest> rettigheter) {
        List<ArenaForvalterNyRettighetResponse> responses = new ArrayList<>();
        for (RettighetRequest rettighet : rettigheter) {
            UriTemplate url;
            if (rettighet instanceof RettighetUngUfoerRequest) {
                url = opprettUngUfoerRettighetUrl;
            } else if (rettighet instanceof RettighetTvungenForvaltningRequest) {
                url = opprettTvungenForvaltningRettighetUrl;
            } else if (rettighet instanceof RettighetFritakMeldekortRequest) {
                url = opprettFritakMeldekortRettighetUrl;
            } else {
                throw new RuntimeException("Unkown URL");
            }
            RequestEntity postRequest = RequestEntity.post(url.expand())
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .body(rettighet);
            responses.add(restTemplate.exchange(postRequest, ArenaForvalterNyRettighetResponse.class).getBody());
        }
        return responses;
    }
}
