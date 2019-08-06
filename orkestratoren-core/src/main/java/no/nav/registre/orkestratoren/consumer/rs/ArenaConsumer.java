package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Objects;

import no.nav.registre.orkestratoren.consumer.rs.response.GenererArenaResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArenaResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Slf4j
@Component
public class ArenaConsumer {

    private static final ParameterizedTypeReference<GenererArenaResponse> RESPONSE_TYPE = new ParameterizedTypeReference<GenererArenaResponse>() {
    };
    private static final ParameterizedTypeReference<SletteArenaResponse> RESPONSE_TYPE_DELETE =
            new ParameterizedTypeReference<SletteArenaResponse>() {
            };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate arenaOpprettArbeidsoekereUrl;
    private UriTemplate arenaSlettArbeidsoekereUrl;

    public ArenaConsumer(@Value("${testnorge.arena.rest.api.url}") String arenaServerUrl) {
        this.arenaOpprettArbeidsoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer");
        this.arenaSlettArbeidsoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/slett?miljoe={miljoe}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<String> opprettArbeidsoekere(SyntetiserArenaRequest syntetiserArenaRequest) {
        RequestEntity postRequest = RequestEntity.post(arenaOpprettArbeidsoekereUrl.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntetiserArenaRequest);

        ResponseEntity<GenererArenaResponse> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);

        return Objects.requireNonNull(response.getBody()).getRegistrerteIdenter();
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public SletteArenaResponse slettIdenter(String miljoe, List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.method(HttpMethod.DELETE, arenaSlettArbeidsoekereUrl.expand(miljoe))
                .contentType(MediaType.APPLICATION_JSON)
                .body(identer);

        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
