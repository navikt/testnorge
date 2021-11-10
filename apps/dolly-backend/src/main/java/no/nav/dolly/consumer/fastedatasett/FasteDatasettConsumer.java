package no.nav.dolly.consumer.fastedatasett;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.StatiskDataForvalterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Component
@Slf4j
public class FasteDatasettConsumer {

    private static final String REQUEST_URL = "/api/v1/faste-data";
    private static final String GRUPPE_REQUEST_URL = REQUEST_URL + "/tps";
    private static final String EREG_REQUEST_URL = REQUEST_URL + "/ereg";
    private static final String GRUPPE_QUERY = "gruppe";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public FasteDatasettConsumer(TokenExchange tokenService, StatiskDataForvalterProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "hentFasteDatasett" })
    public ResponseEntity<JsonNode> hentDatasett(DatasettType datasettType) {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(REQUEST_URL)
                        .pathSegment(datasettType.getUrl())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve().toEntity(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "hentOrgnummer" })
    public ResponseEntity<JsonNode> hentOrgnummer() {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(EREG_REQUEST_URL)
                        .queryParam(GRUPPE_QUERY, "DOLLY")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve().toEntity(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "hentFasteDatasettGruppe" })
    public ResponseEntity<JsonNode> hentDatasettGruppe(String gruppe) {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(GRUPPE_REQUEST_URL)
                        .queryParam(GRUPPE_QUERY, gruppe)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve().toEntity(JsonNode.class)
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
