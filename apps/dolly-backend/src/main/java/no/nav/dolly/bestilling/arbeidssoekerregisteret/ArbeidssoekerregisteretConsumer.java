package no.nav.dolly.bestilling.arbeidssoekerregisteret;

import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.command.LagreTilArbeidsoekerregisteret;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.command.SlettArbeidssoekerregisteretCommand;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretRequest;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.service.CheckAliveService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ArbeidssoekerregisteretConsumer extends ConsumerStatus {

    private final ServerProperties serverProperties;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;

    public ArbeidssoekerregisteretConsumer(
            Consumers consumers,
            WebClient webClient,
            TokenExchange tokenExchange,
            CheckAliveService checkAliveService) {

        super(checkAliveService);
        this.serverProperties = consumers.getArbeidssoekerregisteretProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<ArbeidssoekerregisteretResponse> postArbeidssokerregisteret(ArbeidssoekerregisteretRequest request) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new LagreTilArbeidsoekerregisteret(webClient, request, token.getTokenValue()).call());
    }

    public Mono<HttpStatus> deleteArbeidssokerregisteret(String ident) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new SlettArbeidssoekerregisteretCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-arbeidssoekerregisteret-proxy";
    }
}