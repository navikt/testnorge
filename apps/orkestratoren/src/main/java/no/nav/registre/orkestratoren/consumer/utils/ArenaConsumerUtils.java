package no.nav.registre.orkestratoren.consumer.utils;

import static java.util.Objects.isNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArenaConsumerUtils {

    private final RestTemplate restTemplate;

    public List<NyttVedtakResponse> sendRequest(
            UriTemplate url,
            SyntetiserArenaRequest request,
            String info
    ) {
        if(isNull(request)) {
            return Collections.emptyList();
        }
        var postRequest = RequestEntity.post(url.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);

        Map<String, List<NyttVedtakResponse>> response = new HashMap<>();
        try {
            response = restTemplate.exchange(postRequest, new ParameterizedTypeReference<Map<String, List<NyttVedtakResponse>>>() {
            }).getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Feil under syntetisering av '{}'", info, e);
        }

        List<NyttVedtakResponse> alleOpprettedeRettigheter = new ArrayList<>();
        if (response != null) {
            response.values().forEach(alleOpprettedeRettigheter::addAll);
        }
        return alleOpprettedeRettigheter;
    }
}
