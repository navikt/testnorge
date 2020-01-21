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

import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@Slf4j
@Component
public class RettighetAapArenaForvalterConsumer {

    private final RestTemplate restTemplate;

    private UriTemplate opprettAapRettighetUrl;
    private UriTemplate opprettAap115RettighetUrl;
    private UriTemplate opprettUngUfoerRettighetUrl;
    private UriTemplate opprettTvungenForvaltningRettighetUrl;
    private UriTemplate opprettFritakMeldekortRettighetUrl;

    public RettighetAapArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettAapRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aap");
        this.opprettAap115RettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aap115");
        this.opprettUngUfoerRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapungufor");
        this.opprettTvungenForvaltningRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aaptvungenforvaltning");
        this.opprettFritakMeldekortRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapfritakmeldekort");
    }

    public List<NyttVedtakResponse> opprettRettighet(List<RettighetRequest> rettigheter) {
        List<NyttVedtakResponse> responses = new ArrayList<>(rettigheter.size());
        for (var rettighet : rettigheter) {

            UriTemplate url;
            if (rettighet instanceof RettighetAapRequest) {
                url = opprettAapRettighetUrl;
            } else if (rettighet instanceof RettighetAap115Request) {
                url = opprettAap115RettighetUrl;
            } else if (rettighet instanceof RettighetUngUfoerRequest) {
                url = opprettUngUfoerRettighetUrl;
            } else if (rettighet instanceof RettighetTvungenForvaltningRequest) {
                url = opprettTvungenForvaltningRettighetUrl;
            } else if (rettighet instanceof RettighetFritakMeldekortRequest) {
                url = opprettFritakMeldekortRettighetUrl;
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
