package no.nav.dolly.bestilling.fullmakt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.fullmakt.command.DeleteFullmaktDataCommand;
import no.nav.dolly.bestilling.fullmakt.command.GetFullmaktDataCommand;
import no.nav.dolly.bestilling.fullmakt.command.PostFullmaktDataCommand;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class FullmaktConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public FullmaktConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavFullmaktProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "fullmakt_createData"})
    public Flux<FullmaktResponse> createFullmaktData(List<RsFullmakt> fullmakter, String ident) {

        log.info("Fullmakt opprett  {}", fullmakter);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.fromIterable(fullmakter)
                                .delayElements(Duration.ofMillis(50))
                                .flatMap(fullmakt ->
                                        new PostFullmaktDataCommand(webClient, token.getTokenValue(), ident, fullmakt).call()))
                .doOnNext(fullmaktResponse -> log.info("Fullmakt opprettet for ident {} {}", ident, fullmaktResponse.getMelding()));
    }

    @Timed(name = "providers", tags = {"operation", "fullmakt_getData"})
    public Mono<FullmaktResponse> getFullmaktData(String ident) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new GetFullmaktDataCommand(webClient, ident,
                                token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "fullmakt_getData"})
    public Mono<HttpStatusCode> deleteFullmaktData(String ident, Integer fullmaktId) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new DeleteFullmaktDataCommand(webClient, ident, fullmaktId,
                        token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-fullmakt-proxy";
    }

}
