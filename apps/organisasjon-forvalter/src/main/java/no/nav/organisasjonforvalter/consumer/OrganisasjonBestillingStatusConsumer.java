package no.nav.organisasjonforvalter.consumer;

import io.netty.handler.timeout.ReadTimeoutException;
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
import reactor.netty.http.client.HttpClient;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;

@Slf4j
@Service
public class OrganisasjonBestillingStatusConsumer {

    private static final Duration timeout = Duration.ofSeconds(10);
    private static final String STATUS_URL = "/api/v1/order/{uuid}/items";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonBestillingStatusConsumer(
            @Value("${organisasjon.bestilling.url}") String baseUrl,
            @Value("${organisasjon.bestilling.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client ->
                        client.doOnConnected(conn -> conn
                                .addHandlerLast(new ReadTimeoutHandler((int) (timeout.toSeconds() / 2)))
                                .addHandlerLast(new WriteTimeoutHandler((int) (timeout.toSeconds() / 2)))));

        this.webClient = WebClient.builder().baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public List<ItemDto> getBestillingStatus(String uuid) {

        try {
            long startTime = currentTimeMillis();
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<ItemDto[]> response = webClient.get()
                    .uri(STATUS_URL.replace("{uuid}", uuid))
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .retrieve()
                    .toEntity(ItemDto[].class)
                    .block();

            log.info("Organisasjon-bestilling-status tok {} ms", currentTimeMillis() - startTime);
            return response.hasBody() ? List.of(response.getBody()) : emptyList();

        } catch (ReadTimeoutException e) {

            log.error("Bestilling kunne ikke utføres, mulig Kafka-problem", e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT,
                    "Bestilling kunne ikke utføres, mulig Kafka-problem");
        }
    }

    public enum ItemStatus {NOT_STARTED, RUNNING, COMPLETED, ERROR, FAILED}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {

        private Integer id;
        private ItemStatus status;

        @Override
        public String toString() {
            return "ItemDto{" +
                    "id=" + id +
                    ", status=" + status +
                    '}';
        }
    }
}
