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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class OrganisasjonNummerConsumer {

    private static final int TIMEOUT_MS = 10_000;
    private static final String NUMBER_URL = "/api/v1/orgnummer";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonNummerConsumer(
            @Value("${organisasjon.nummer.url}") String baseUrl,
            @Value("${organisasjon.nummer.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .tcpConfiguration(tcpClient -> tcpClient
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_MS)
                                        .doOnConnected(connection ->
                                                connection
                                                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_MS))
                                                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_MS))))))
                .build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public List<String> getOrgnummer(Integer antall) {

        long startTime = currentTimeMillis();
        try {
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<List> response = webClient.get()
                    .uri(NUMBER_URL)
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .header("antall", antall.toString())
                    .retrieve()
                    .toEntity(List.class)
                    .block();

            log.info("Orgnummer-service svarte etter {} ms", currentTimeMillis() - startTime);

            return response.hasBody() ? response.getBody() : Collections.emptyList();

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY, e.getMessage());

        } catch (RuntimeException e) {
            String error = format("Organisasjon-orgnummer-service svarte ikke etter %d ms", currentTimeMillis() - startTime);
            log.error(error, e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, error);
        }
    }

    public String getOrgnummer() {

        List<String> orgnummer = getOrgnummer(1);
        return orgnummer.isEmpty() ? null : orgnummer.get(0);
    }
}
