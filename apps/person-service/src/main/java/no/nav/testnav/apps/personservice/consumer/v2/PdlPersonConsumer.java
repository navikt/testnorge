package no.nav.testnav.apps.personservice.consumer.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.apps.personservice.config.Consumers;
import no.nav.testnav.apps.personservice.consumer.v2.commad.PdlBolkPersonCommand;
import no.nav.testnav.apps.personservice.consumer.v2.commad.PdlPersonGetCommand;
import no.nav.testnav.apps.personservice.provider.v2.PdlMiljoer;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.nav.testnav.apps.personservice.consumer.v2.JacksonExchangeStrategyUtil.getJacksonStrategy;

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
            WebClient webClient
    ) {
        serverProperties = consumers.getPdlProxy();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    public Mono<JsonNode> getPdlPerson(String ident, PdlMiljoer pdlMiljoe) {

        return tokenService.exchange(serverProperties)
                .flatMap((AccessToken token) -> new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe)
                        .call());
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
