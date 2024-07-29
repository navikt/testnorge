package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.HentTagsCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.List;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class HentTagsConsumer {
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public HentTagsConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        serverProperties = consumers.getPdlProxy();

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
    }

    public void hentTags(String[] identer) throws Exception {
        var token = tokenService.exchange(serverProperties).block();
        JsonNode tags = new HentTagsCommand(webClient, token.getTokenValue(), identer).call();
        log.info("Hentet tags {}", Json.pretty(tags));
    }
}
