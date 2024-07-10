package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.command.PdlBolkPersonCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.command.PdlPersonGetCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.v2.PdlMiljoer;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
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
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlPersonConsumer {

    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public PdlPersonConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        log.info("PdlPersonConsumer");
        serverProperties = consumers.getTestnavPdlProxy();
        log.info("serverProperties: {}", serverProperties);
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

    public Mono<JsonNode> getPdlPerson(String ident, PdlMiljoer pdlMiljoe) {
        log.info("getPdlPerson");
        var token = tokenService.exchange(serverProperties).block();
        log.info("token: {}", token);
        log.info("TokenVaule: {}", token.getTokenValue());

        return new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe).call();
        /*
        return tokenService.exchange(serverProperties)
                .flatMap((AccessToken token) -> new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe)
                        .call());

         */
    }

    public Mono<JsonNode> getPdlPersoner(List<String> identer) {

        return tokenService.exchange(serverProperties)
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
