package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.HentTagsCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.SokPersonCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.SokPersonPagesCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
//import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
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
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlConsumer {

    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public PdlConsumer(
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

    public Mono<JsonNode> getSokPerson(
            GraphqlVariables.Paging paging,
            GraphqlVariables.Criteria criteria,
            PdlMiljoer pdlMiljoe) {
        return tokenService.exchange(serverProperties)
                .flatMap((AccessToken token) -> new SokPersonCommand(webClient, paging, criteria, token.getTokenValue(), pdlMiljoe)
                        .call());
    }

    public Mono<JsonNode> getSokPersonPages(
            GraphqlVariables.Paging paging,
            GraphqlVariables.Criteria criteria,
            PdlMiljoer pdlMiljoe) {
        return tokenService.exchange(serverProperties)
                .flatMap((AccessToken token) -> new SokPersonPagesCommand(webClient, paging, criteria, token.getTokenValue(), pdlMiljoe)
                        .call());
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

    public void hentTags(String[] identer) throws Exception {
        var token = tokenService.exchange(serverProperties).block();
        JsonNode tags = new HentTagsCommand(webClient, token.getTokenValue() ,identer).call();
    }
}
