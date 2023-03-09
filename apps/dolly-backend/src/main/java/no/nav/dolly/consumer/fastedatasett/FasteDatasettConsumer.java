package no.nav.dolly.consumer.fastedatasett;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.StatiskDataForvalterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Component
@Slf4j
public class FasteDatasettConsumer {

    private static final String REQUEST_URL = "/api/v1/faste-data";
    private static final String GRUPPE_REQUEST_URL = REQUEST_URL + "/tps";
    private static final String EREG_REQUEST_URL = REQUEST_URL + "/ereg";
    private static final String GRUPPE_QUERY = "gruppe";
    private static final String BEARER = "Bearer ";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;

    public FasteDatasettConsumer(
            TokenExchange tokenService,
            StatiskDataForvalterProxyProperties serverProperties,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "hentFasteDatasett"})
    public ResponseEntity<JsonNode> hentDatasett(DatasettType datasettType) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient.get().uri(uriBuilder -> uriBuilder
                                .path(REQUEST_URL)
                                .pathSegment(datasettType.getUrl())
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)))
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "hentOrgnummer"})
    public ResponseEntity<JsonNode> hentOrgnummer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient.get().uri(uriBuilder -> uriBuilder
                                .path(EREG_REQUEST_URL)
                                .queryParam(GRUPPE_QUERY, "DOLLY")
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .retrieve().toEntity(JsonNode.class))
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "hentFasteDatasettGruppe"})
    public ResponseEntity<JsonNode> hentDatasettGruppe(String gruppe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient.get().uri(uriBuilder -> uriBuilder
                                .path(GRUPPE_REQUEST_URL)
                                .queryParam(GRUPPE_QUERY, gruppe)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, BEARER + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .retrieve().toEntity(JsonNode.class))
                .block();
    }
}
