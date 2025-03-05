package no.nav.testnav.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.pdl.SokPersonCommand;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.PdlPersonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.testnav.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
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
            ObjectMapper objectMapper,
            WebClient webClient
    ) {
        serverProperties = consumers.getPdlProxy();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> {
                            config.defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config.defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                        .build())
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient
                                        .create(ConnectionProvider.builder("PDL connections")
                                                .maxConnections(1)
                                                .pendingAcquireMaxCount(10000)
                                                .pendingAcquireTimeout(Duration.ofSeconds(3000))
                                                .build())))
                .build();
    }

    public Flux<PdlPersonDTO> getSokPerson(
            GraphqlVariables.Paging paging,
            GraphqlVariables.Criteria criteria,
            PdlMiljoer pdlMiljoe) {

        return tokenService.exchange(serverProperties)
                .flatMapMany((AccessToken token) -> new SokPersonCommand(webClient, paging, criteria,
                        token.getTokenValue(), pdlMiljoe).call());
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
