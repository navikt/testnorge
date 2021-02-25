package no.nav.organisasjonforvalter.consumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class MiljoerServiceConsumer {

    private static final int TIMEOUT_S = 10;
    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public MiljoerServiceConsumer(
            @Value("${miljoer.service.url}") String baseUrl,
            @Value("${miljoer.service.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .tcpConfiguration(tcpClient -> tcpClient
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_S * 1000)
                                        .doOnConnected(connection ->
                                                connection
                                                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_S))
                                                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_S))))))
                .build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public Set<String> getOrgMiljoer() {

        try {
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<String[]> response = webClient.get()
                    .uri(MILJOER_URL)
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .retrieve()
                    .toEntity(String[].class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                    .block();

            return nonNull(response) && response.hasBody() ?
                    List.of(response.getBody()).stream()
                            .filter(env -> !env.equals("u5") && !env.equals("qx"))
                            .collect(Collectors.toSet()) :
                    emptySet();

        } catch (RuntimeException e) {
            log.error("Feilet å hente miljøer fra miljoer-service", e);
            return emptySet();
        }
    }
}
