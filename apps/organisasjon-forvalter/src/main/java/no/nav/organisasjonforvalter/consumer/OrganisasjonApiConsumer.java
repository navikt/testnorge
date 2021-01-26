package no.nav.organisasjonforvalter.consumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class OrganisasjonApiConsumer {

    private static final int TIMEOUT_MS = 10_000;
    private static final String STATUS_URL = "/api/v1/organisasjoner/{orgnummer}";
    private static final String MILJOE = "miljo";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonApiConsumer(
            @Value("${organisasjon.api.url}") String baseUrl,
            @Value("${organisasjon.api.client.id}") String clientId,
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

    public Response getStatus(String orgnummer, String miljoe) {

        long startTime = currentTimeMillis();

        try {
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<Response> response = webClient.get()
                    .uri(STATUS_URL.replace("{orgnummer}", orgnummer))
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .header(MILJOE, miljoe)
                    .retrieve()
                    .toEntity(Response.class)
                    .block();

            log.info("Organisasjon-API svarte med funnet etter {} ms", currentTimeMillis() - startTime);
            return response.hasBody() ? response.getBody() : new Response();

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY, e.getMessage());

        } catch (RuntimeException e) {
            String error = format("Testnorge-organisasjon-api svarte ikke etter %d ms", currentTimeMillis() - startTime);
            log.error(error, e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, error);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String orgnummer;
        private List<String> driverVirksomheter;
    }
}
