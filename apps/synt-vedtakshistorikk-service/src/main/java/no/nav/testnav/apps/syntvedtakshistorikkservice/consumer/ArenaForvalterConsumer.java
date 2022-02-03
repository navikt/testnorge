package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PostEndreInnsatsbehovCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PostFinnTiltakCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PostRettighetCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.ArenaForvalterenProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.EndreInnsatsbehovRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.FinnTiltakRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ArenaForvalterConsumer {

    private final WebClient webClient;

    public ArenaForvalterConsumer(
            ArenaForvalterenProperties properties
    ) {
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
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

    public Mono<NyttVedtakResponse> finnTiltak(FinnTiltakRequest rettighet) {
        return new PostFinnTiltakCommand(rettighet, webClient).call();
    }

    public void endreInnsatsbehovForBruker(EndreInnsatsbehovRequest endreRequest) {
        new PostEndreInnsatsbehovCommand(endreRequest, webClient)
                .call()
                .flatMap(res -> {
                    var feil = res.getNyeEndreInnsatsbehovFeilList();
                    if (feil != null && !feil.isEmpty()) {
                        return Mono.empty();
                    }
                    return Mono.just(res);
                }).switchIfEmpty(Mono.defer(() -> {
                    log.info(String.format("Endring av innsatsbehov for ident %s feilet", endreRequest.getPersonident()));
                    return Mono.empty();
                }));
    }
}
