package no.nav.registre.sdforvalter.consumer.rs.tpsf;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.command.GetMeldingsIdsCommand;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.command.PostSendSkdMeldingerTpsCommand;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.request.SendToTpsRequest;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class TpsfConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public TpsfConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getTpsForvalterenProxy();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    private Mono<SkdMeldingerTilTpsRespons> sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest) {

        log.info("Sender skd-meldinger med avspillergruppe {} til tps", gruppeId);
        return tokenExchange.exchange(serverProperties).flatMap(accessToken ->
                new PostSendSkdMeldingerTpsCommand(gruppeId, sendToTpsRequest, webClient, accessToken.getTokenValue()).call());
    }

    private Mono<List<Long>> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetMeldingsIdsCommand(gruppeId, webClient, accessToken.getTokenValue()).call());
    }

    public Mono<SkdMeldingerTilTpsRespons> sendTilTps(Long gruppeId, String miljoe) {
        return getMeldingIdsFromAvspillergruppe(gruppeId)
                .flatMap(meldingIds -> sendSkdmeldingerToTps(gruppeId, new SendToTpsRequest(miljoe, meldingIds)));
    }
}