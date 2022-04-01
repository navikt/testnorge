package no.nav.dolly.bestilling.krrstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.krrstub.command.DeleteKontaktadataCommand;
import no.nav.dolly.bestilling.krrstub.command.GetKontaktdataCommand;
import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Service
public class KrrstubConsumer {

    private static final String DIGITAL_KONTAKT_URL = "/api/v2/kontaktinformasjon";

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public KrrstubConsumer(TokenExchange tokenService, KrrstubProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @Timed(name = "providers", tags = {"operation", "krrstub_createKontaktdata"})
    public ResponseEntity<Object> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(digitalKontaktdata)
                .retrieve()
                .toEntity(Object.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    public Mono<List<String>> deleteKontaktdata(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .map(idx -> new GetKontaktdataCommand(webClient, identer.get(idx), token.getTokenValue()).call())
                        .flatMap(Flux::from)
                        .next()
                        .map(resp -> {
                            if (nonNull(resp.getId())) {
                                new DeleteKontaktadataCommand(webClient, resp.getId(), token.getTokenValue()).call();
                                return format("Slettet ident med id=%d", resp.getId());
                            } else {
                                return " ";
                            }
                        }))
                .filter(StringUtils::isNotBlank)
                .collectList();
    }

    public List<DigitalKontaktdata> getKontaktdata(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new GetKontaktdataCommand(webClient, ident, token.getTokenValue()).call())
                .collectList()
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}