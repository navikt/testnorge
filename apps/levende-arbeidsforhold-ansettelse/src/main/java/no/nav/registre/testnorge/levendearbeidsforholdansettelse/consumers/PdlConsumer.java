package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.SokPersonCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.SokPersonPagesCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
public class PdlConsumer {

    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public PdlConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getPdlProxy();
        this.tokenService = tokenService;
        webClient = webClientBuilder
            .baseUrl(serverProperties.getUrl())
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
}
