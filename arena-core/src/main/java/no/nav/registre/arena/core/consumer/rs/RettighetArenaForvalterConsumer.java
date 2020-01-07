package no.nav.registre.arena.core.consumer.rs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.domain.rettighet.NyRettighetResponse;

@Slf4j
@Component
public class RettighetArenaForvalterConsumer {

    private static final String NAV_CALL_ID = "ORKESTRATOREN";
    private static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    private final RestTemplate restTemplate;

    private UriTemplate opprettAapRettighetUrl;
    private UriTemplate opprettUngUfoerRettighetUrl;
    private UriTemplate opprettTvungenForvaltningRettighetUrl;
    private UriTemplate opprettFritakMeldekortRettighetUrl;

    public RettighetArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettAapRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aap");
        this.opprettUngUfoerRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapungufor");
        this.opprettTvungenForvaltningRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aaptvungenforvaltning");
        this.opprettFritakMeldekortRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapfritakmeldekort");
    }

    public List<NyRettighetResponse> opprettRettighet(List<RettighetRequest> rettigheter) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<NyRettighetResponse> responses = new ArrayList<>();
        for (var rettighet : rettigheter) {
            UriTemplate url;
            if (rettighet instanceof RettighetAapRequest) {
                url = opprettAapRettighetUrl;
            } else if (rettighet instanceof RettighetUngUfoerRequest) {
                url = opprettUngUfoerRettighetUrl;
            } else if (rettighet instanceof RettighetTvungenForvaltningRequest) {
                url = opprettTvungenForvaltningRettighetUrl;
            } else if (rettighet instanceof RettighetFritakMeldekortRequest) {
                url = opprettFritakMeldekortRettighetUrl;
            } else {
                throw new RuntimeException("Unkown URL");
            }
            try {
                log.info("Legger til syntetisk rettighet: {}", objectMapper.writeValueAsString(rettighet));
            } catch (JsonProcessingException e) {
                log.error("Kunne ikke printe rettighet", e);
            }
            var postRequest = RequestEntity.post(url.expand())
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .body(rettighet);
            responses.add(restTemplate.exchange(postRequest, NyRettighetResponse.class).getBody());
        }
        return responses;
    }
}
