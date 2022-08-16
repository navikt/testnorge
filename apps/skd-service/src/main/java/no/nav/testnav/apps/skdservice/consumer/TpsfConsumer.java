package no.nav.testnav.apps.skdservice.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.skdservice.consumer.command.GetMeldingsIdsCommand;
import no.nav.testnav.apps.skdservice.consumer.command.PostSendSkdMeldingerTpsCommand;
import no.nav.testnav.apps.skdservice.consumer.command.requests.SendToTpsRequest;
import no.nav.testnav.apps.skdservice.consumer.credential.TpsfProxyProperties;
import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class TpsfConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final TpsfProxyProperties serviceProperties;

    public TpsfConsumer(
            TpsfProxyProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
        this.tokenExchange = tokenExchange;
    }

    @Timed(value = "skd.resource.latency", extraTags = {"operation", "tpsf"})
    public Mono<SkdMeldingerTilTpsRespons> sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest) {

        log.info("Sender skd-meldinger med avspillergruppe {} til tps", gruppeId);
        return tokenExchange.exchange(serviceProperties).flatMap(accessToken ->
                        new PostSendSkdMeldingerTpsCommand(gruppeId, sendToTpsRequest, webClient, accessToken.getTokenValue()).call());
    }

    @Timed(value = "skd.resource.latency", extraTags = {"operation", "tpsf"})
    public Mono<List<Long>> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetMeldingsIdsCommand(gruppeId, webClient, accessToken.getTokenValue()).call());
    }
}
