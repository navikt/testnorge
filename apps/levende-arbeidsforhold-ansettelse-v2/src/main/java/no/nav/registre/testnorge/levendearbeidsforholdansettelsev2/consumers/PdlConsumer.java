package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.config.Consumers;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.List;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlConsumer {
/*
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;



 */
    public PdlConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        /*
        serverProperties = consumers.getTestnavPdlProxy();
        this.tokenService = tokenService;
        webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create(
                                                ConnectionProvider
                                                        .builder("custom")
                                                        .maxConnections(10)
                                                        .pendingAcquireMaxCount(5000)
                                                        .pendingAcquireTimeout(Duration.ofMinutes(15))
                                                        .build())
                                        .responseTimeout(Duration.ofSeconds(5))))
                .build();

         */
    }
/*
    public Mono<JsonNode> getPdlPerson(String ident, PdlMiljoer pdlMiljoe) {

        return tokenService.exchange(serverProperties)
                .flatMap((AccessToken token) -> new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe)
                        .call());
    }

    public Mono<JsonNode> getPdlPersoner(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PdlBolkPersonCommand(webClient, identer, token.getTokenValue()).call());
    }

 */
}
