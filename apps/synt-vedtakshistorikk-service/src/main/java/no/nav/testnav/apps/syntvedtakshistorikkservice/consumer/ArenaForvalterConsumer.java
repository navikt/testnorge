package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PostEndreInnsatsbehovCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PostFinnTiltakCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PostRettighetCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.ArenaForvalterenProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.EndreInnsatsbehovRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.FinnTiltakRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ArenaForvalterConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public ArenaForvalterConsumer(
            ArenaForvalterenProxyProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public Map<String, List<NyttVedtakResponse>> opprettRettighet(List<RettighetRequest> rettigheter) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        for (var rettighet : rettigheter) {
            var response = tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new PostRettighetCommand(rettighet, accessToken.getTokenValue(), webClient).call())
                    .block();

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

    public NyttVedtakResponse finnTiltak(FinnTiltakRequest rettighet) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new PostFinnTiltakCommand(rettighet,accessToken.getTokenValue(), webClient).call())
                .block();
    }

    public void endreInnsatsbehovForBruker(EndreInnsatsbehovRequest endreRequest) {
        var response =  tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new PostEndreInnsatsbehovCommand(endreRequest, accessToken.getTokenValue(), webClient).call())
                .block();

        if (response == null || (response.getNyeEndreInnsatsbehovFeilList() != null &&
                !response.getNyeEndreInnsatsbehovFeilList().isEmpty())) {
            log.info(String.format("Endring av innsatsbehov for ident %s feilet", endreRequest.getPersonident()));
        }
    }
}
