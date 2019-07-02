package no.nav.registre.arena.core.consumer.rs;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.domain.NyeBrukereList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.text.SimpleDateFormat;


@Component
@Slf4j
public class ArenaForvalterConsumer {

    private String EIER = "Dolly";
    private String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate postBrukere;

    public ArenaForvalterConsumer(@Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl) {
        this.postBrukere = new UriTemplate(arenaForvalterServerUrl + "/v1/bruker?eier=" + EIER);
    }


    @Timed(value = "arena.resource.latency", extraTags = {"operation", "arena-forvalteren"})
    public StatusFraArenaForvalterResponse sendTilArenaForvalter(NyeBrukereList nyeBrukere)
            throws JsonProcessingException {
        RequestEntity postRequest = RequestEntity.post(postBrukere.expand())
                .header("Nav-Call-Id", "ORKESTRATOREN")
                .header("Nav-Consumer-Id", "ORKESTRATOREN")
                .body(createJsonRequestBody(nyeBrukere));
        ResponseEntity<StatusFraArenaForvalterResponse> response = restTemplate.exchange(postRequest,
                StatusFraArenaForvalterResponse.class);


        if (response.getBody() == null) {
            log.error("ArenaForvalterConsumer.sendTilArenaForvalter: Kunne ikke hente response body fra Arena Forvalteren: NullPointerException");
        } else {
            return response.getBody();
        }

        return StatusFraArenaForvalterResponse.builder().build();
    }


    private String createJsonRequestBody(NyeBrukereList nyeBrukere) throws JsonProcessingException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(sdf);

        return objectMapper.writeValueAsString(nyeBrukere);
    }
}
