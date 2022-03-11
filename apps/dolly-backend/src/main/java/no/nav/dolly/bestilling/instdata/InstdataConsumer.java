package no.nav.dolly.bestilling.instdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.config.credentials.InstProxyProperties;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
public class InstdataConsumer {

    private static final String INSTDATA_URL = "/api/v1/ident";
    private static final String DELETE_POST_FMT_BLD = INSTDATA_URL + "/batch";
    private static final String INSTMILJO_URL = "/api/v1/miljoer";

    private static final String INST_IDENTER_QUERY = "identer";
    private static final String INST_MILJOE_QUERY = "miljoe";

    private static final String DELETE_ERROR = "Feilet å slette person: {}, i INST miljø: {}";

    private static final List<String> DEFAULT_ENV = List.of("q2");

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public InstdataConsumer(TokenExchange tokenService, InstProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "inst_getMiljoer" })
    public List<String> getMiljoer() {

        try {
            String[] response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(INSTMILJO_URL)
                            .build())
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .retrieve().bodyToMono(String[].class)
                    .block();

            if (isNull(response)) {
                log.warn("Klarte ikke å hente miljøer fra testnorge-inst");
                return DEFAULT_ENV;
            }

            return Arrays.asList(response);

        } catch (RuntimeException e) {
            log.error("Kunne ikke lese fra endepunkt for aa hente miljoer: {} ", e.getMessage(), e);
            return DEFAULT_ENV;
        }
    }

    @Timed(name = "providers", tags = { "operation", "inst_getInstdata" })
    public ResponseEntity<List<Instdata>> getInstdata(String ident, String environment) {
        return
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(INSTDATA_URL)
                                .queryParam(INST_IDENTER_QUERY, ident)
                                .queryParam(INST_MILJOE_QUERY, environment)
                                .build())
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .retrieve().toEntityList(Instdata.class)
                        .block();
    }

    @Timed(name = "providers", tags = { "operation", "inst_deleteInstdata" })
    public void deleteInstdata(String ident, String environment) {

        try {
            ResponseEntity<List<InstdataResponse>> response =
                    webClient.delete()
                            .uri(uriBuilder -> uriBuilder
                                    .path(DELETE_POST_FMT_BLD)
                                    .queryParam(INST_IDENTER_QUERY, ident)
                                    .queryParam(INST_MILJOE_QUERY, environment)
                                    .build())
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                            .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                            .retrieve()
                            .toEntityList(InstdataResponse.class)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                    .filter(WebClientFilter::is5xxException))
                            .block();

            if (nonNull(response) && response.hasBody() && !response.getBody().isEmpty() &&
                    !NOT_FOUND.equals(response.getBody().get(0).getStatus()) &&
                    !OK.equals(response.getBody().get(0).getStatus())) {

                log.error(DELETE_ERROR, ident, environment);
            }

        } catch (RuntimeException e) {
            log.error(DELETE_ERROR, ident, environment, e);
        }
    }

    @Timed(name = "providers", tags = { "operation", "inst_postInstdata" })
    public ResponseEntity<List<InstdataResponse>> postInstdata(List<Instdata> instdata, String environment) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_POST_FMT_BLD)
                        .queryParam(INST_MILJOE_QUERY, environment)
                        .build())
                .bodyValue(instdata)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toEntityList(InstdataResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
