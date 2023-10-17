package no.nav.testnav.apps.personservice.consumer.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.apps.personservice.config.credentials.PdlProxyProperties;
import no.nav.testnav.apps.personservice.consumer.v2.commad.PdlBolkPersonCommand;
import no.nav.testnav.apps.personservice.consumer.v2.commad.PdlPersonGetCommand;
import no.nav.testnav.apps.personservice.provider.v2.PdlMiljoer;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.testnav.apps.personservice.consumer.v2.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlPersonConsumer {

    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public PdlPersonConsumer(
            TokenExchange tokenService,
            PdlProxyProperties serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create(ConnectionProvider.builder("custom")
                                .maxConnections(10)
                                .pendingAcquireMaxCount(5000)
                                .pendingAcquireTimeout(Duration.ofMinutes(15))
                                .build())
                                .responseTimeout(Duration.ofSeconds(5))))
                .build();
    }

    public Mono<JsonNode> getPdlPerson(String ident, PdlMiljoer pdlMiljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap((AccessToken token) -> new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe)
                        .call());
    }

    public Mono<JsonNode> getPdlPersoner(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new PdlBolkPersonCommand(webClient, identer, token.getTokenValue()).call());
    }

    public static String hentQueryResource(String pathResource) {
        val resource = new ClassPathResource(pathResource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", pathResource, e);
            return null;
        }
    }
}
