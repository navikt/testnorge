package no.nav.registre.sdforvalter.consumer.rs.tpsf;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.sdforvalter.config.credentials.TpsfProxyProperties;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.command.GetMeldingsIdsCommand;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.command.PostSendSkdMeldingerTpsCommand;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.request.SendToTpsRequest;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
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

    public Mono<SkdMeldingerTilTpsRespons> sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest) {

        log.info("Sender skd-meldinger med avspillergruppe {} til tps", gruppeId);
        return tokenExchange.exchange(serviceProperties).flatMap(accessToken ->
                        new PostSendSkdMeldingerTpsCommand(gruppeId, sendToTpsRequest, webClient, accessToken.getTokenValue()).call());
    }

    private Mono<List<Long>> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetMeldingsIdsCommand(gruppeId, webClient, accessToken.getTokenValue()).call());
    }

    public Mono<SkdMeldingerTilTpsRespons> sendTilTps(Long gruppeId, String miljoe){
        return getMeldingIdsFromAvspillergruppe(gruppeId)
                .flatMap(meldingIds -> sendSkdmeldingerToTps(gruppeId, new SendToTpsRequest(miljoe, meldingIds)));
    }
}
