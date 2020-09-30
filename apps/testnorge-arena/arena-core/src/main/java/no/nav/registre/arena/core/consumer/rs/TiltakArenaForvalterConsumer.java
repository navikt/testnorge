package no.nav.registre.arena.core.consumer.rs;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@DependencyOn(value = "arena-forvalteren", external = true)
public class TiltakArenaForvalterConsumer {

    private final RestTemplate restTemplate;

    private String arenaForvalterServerUrl;

    public TiltakArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.arenaForvalterServerUrl = arenaForvalterServerUrl;
    }

    public List<NyttVedtakTiltak> finnTiltak(List<NyttVedtakTiltak> tiltaksdeltakelser) {
        var responses = new ArrayList<NyttVedtakTiltak>();
        List<Integer> tiltaksIder = new ArrayList<>();

        var url = new UriTemplate(arenaForvalterServerUrl + "tiltaksdeltakelse/finn_tiltak");

        for (NyttVedtak deltakelse: tiltaksdeltakelser){
            var postRequest = RequestEntity.post(url.expand())
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .body(deltakelse);
            NyttVedtakTiltak response = null;
            try {
                response = restTemplate.exchange(postRequest, NyttVedtakTiltak.class).getBody();
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke finne tiltak i arena-forvalteren.", e);
            }
            if (response != null && response.getTiltakId() != null && !tiltaksIder.contains(response.getTiltakId())) {
                tiltaksIder.add(response.getTiltakId());
                responses.add(response);
            }
        }

        return responses;
    }

}
