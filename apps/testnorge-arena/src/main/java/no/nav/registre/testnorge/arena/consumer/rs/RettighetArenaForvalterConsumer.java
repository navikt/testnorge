package no.nav.registre.testnorge.arena.consumer.rs;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.arena.consumer.rs.command.PostFinnTiltakCommand;
import no.nav.registre.testnorge.arena.consumer.rs.command.PostRettighetCommand;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RettighetArenaForvalterConsumer {

    private final WebClient webClient;

    public RettighetArenaForvalterConsumer(
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaForvalterServerUrl).build();
    }

    public Map<String, List<NyttVedtakResponse>> opprettRettighet(List<RettighetRequest> rettigheter) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        for (var rettighet : rettigheter) {
            var response = new PostRettighetCommand(rettighet, webClient).call();

            if (response != null) {
                if (responses.containsKey(rettighet.getPersonident())) {
                    responses.get(rettighet.getPersonident()).add(response);
                } else {
                    responses.put(rettighet.getPersonident(), new ArrayList<>(Collections.singletonList(response)));
                }
            }
            if (response == null || (response.getFeiledeRettigheter() != null && !response.getFeiledeRettigheter().isEmpty())) {
                log.info("Innsendt rettighet feilet. Stopper videre innsending av historikk for ident: "
                        + rettighet.getPersonident());
                break;
            }
        }
        return responses;
    }

    public NyttVedtakResponse finnTiltak(RettighetFinnTiltakRequest rettighet) {
        return new PostFinnTiltakCommand(rettighet, webClient).call();
    }
}
