package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivGetMiljoeCommand;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.config.credentials.DokarkivProxyServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class DokarkivConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public DokarkivConsumer(DokarkivProxyServiceProperties properties,
                            TokenExchange tokenService,
                            ObjectMapper objectMapper,
                            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = properties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv-opprett" })
    public Mono<DokarkivResponse> postDokarkiv(String environment, DokarkivRequest dokarkivRequest) {

        String callId = getNavCallId();
        log.info("Sender dokarkiv melding: callId: {}, consumerId: {}, miljø: {}", callId, CONSUMER, environment);

        return webClient.post()
                .uri(builder ->
                        builder.path("/api/{miljo}/v1/journalpost").build(environment))
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(dokarkivRequest)
                .retrieve()
                .bodyToMono(DokarkivResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(DokarkivResponse.builder()
                        .feilmelding(WebClientFilter.getMessage(error))
                        .build()));
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv_getEnvironments" })
    public Mono<List<String>> getEnvironments() {
        return new DokarkivGetMiljoeCommand(webClient, serviceProperties.getAccessToken(tokenService)).call();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-dokarkiv-proxy";
    }

}
