package no.nav.registre.arena.core.consumer.rs;


import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.domain.NyBruker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Component
@Slf4j
public class ArenaForvalterConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate postBrukere;

    public ArenaForvalterConsumer(@Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl) {
        this.postBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker");
    }


    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public StatusFraArenaForvalterResponse sendTilArenaForvalter(List<NyBruker> nyeBrukere) {
        RequestEntity postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", "ORKESTRATOREN")
                .header("Nav-Consumer-Id", "ORKESTRATOREN")
                .body(nyeBrukere);
        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(postRequest,
                StatusFraArenaForvalterResponse.class);


        if (response.getBody() == null) {
            log.error("ArenaForvalterConsumer.sendTilArenaForvalter: Kunne ikke hente response body fra Arena Forvalteren: NullPointerException");
        } else {
            return response.getBody();
        }

        return StatusFraArenaForvalterResponse.builder().build();
    }

}
