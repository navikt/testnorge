package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererArenaResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArenaResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

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

    private UriTemplate arenaOpprettArbeidsokereUrl;
    private UriTemplate arenaSlettArbeidsokereUrl;

    public ArenaConsumer(@Value("${testnorge-arena.rest-api.url}") String arenaServerUrl) {
        this.arenaOpprettArbeidsokereUrl = new UriTemplate(arenaServerUrl + "/v1/generer");
        this.arenaSlettArbeidsokereUrl = new UriTemplate(arenaServerUrl + "/v1/slett?miljoe={miljoe}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = {"operation", "arena"})
    public List<String> opprettArbeidsokere(SyntetiserArenaRequest syntetiserArenaRequest) {
        RequestEntity postRequest = RequestEntity.post(arenaOpprettArbeidsokereUrl.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntetiserArenaRequest);

        List<String> opprettedeIdenter;
        ResponseEntity<GenererArenaResponse> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);

        if (response != null && response.getBody() != null)
            opprettedeIdenter = response.getBody().getRegistrerteIdenter();
        else {
            log.error("Kunne ikke hente response body fra testnorge-arena/generer: NullpointerException");
            opprettedeIdenter = new ArrayList<>();
        }

        return opprettedeIdenter;
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = {"operation", "arena"})
    public SletteArenaResponse slettIdenter(String miljoe, List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.method(HttpMethod.DELETE, arenaSlettArbeidsokereUrl.expand(miljoe))
                .contentType(MediaType.APPLICATION_JSON)
                .body(identer);

        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
