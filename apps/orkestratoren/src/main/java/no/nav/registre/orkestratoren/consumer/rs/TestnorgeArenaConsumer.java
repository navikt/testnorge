package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.consumer.rs.response.GenererArenaResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArenaResponse;
import no.nav.registre.orkestratoren.exception.ArenaSyntetiseringException;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Slf4j
@Component
public class TestnorgeArenaConsumer {

    private static final ParameterizedTypeReference<GenererArenaResponse> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<SletteArenaResponse> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final UriTemplate arenaOpprettArbeidsoekereUrl;
    private final UriTemplate arenaOpprettArbeidsoekereMedOppfoelgingUrl;
    private final UriTemplate arenaSlettArbeidsoekereUrl;

    public TestnorgeArenaConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-arena.rest.api.url}") String arenaServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.arenaOpprettArbeidsoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer");
        this.arenaOpprettArbeidsoekereMedOppfoelgingUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/oppfoelging");
        this.arenaSlettArbeidsoekereUrl = new UriTemplate(arenaServerUrl + "/v1/ident/slett?miljoe={miljoe}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = {"operation", "arena"})
    public List<String> opprettArbeidsoekere(
            SyntetiserArenaRequest syntetiserArenaRequest,
            boolean medOppfoelging
    ) {
        var url = medOppfoelging ? arenaOpprettArbeidsoekereMedOppfoelgingUrl : arenaOpprettArbeidsoekereUrl;
        var postRequest = RequestEntity.post(url.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntetiserArenaRequest);

        var response = restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();

        if (response != null) {
            return response.getRegistrerteIdenter();
        } else {
            throw new ArenaSyntetiseringException("Feil i syntetisering av arena");
        }
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = {"operation", "arena"})
    public SletteArenaResponse slettIdenter(
            String miljoe,
            List<String> identer
    ) {
        var deleteRequest = RequestEntity.method(HttpMethod.DELETE, arenaSlettArbeidsoekereUrl.expand(miljoe))
                .contentType(MediaType.APPLICATION_JSON)
                .body(identer);

        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
