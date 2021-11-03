package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.config.credentials.DokarkivProxyServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class DokarkivConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public DokarkivConsumer(DokarkivProxyServiceProperties properties, TokenExchange tokenService, ObjectMapper objectMapper) {
        this.serviceProperties = properties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv-opprett" })
    public Mono<DokarkivResponse> postDokarkiv(String environment, DokarkivRequest dokarkivRequest) {

        String callId = getNavCallId();
        log.info("Sender dokarkiv melding: callId: {}, consumerId: {}, miljø: {}", callId, CONSUMER, environment);

        return webClient.post()
                .uri(builder -> builder.path("/api/{miljo}/v1/journalpost").build(environment))
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(dokarkivRequest)
                .retrieve()
                .bodyToMono(DokarkivResponse.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException webClientResponseException) {
                        log.error(
                                "Feil ved opprettelse av journalpost av med body: {}.",
                                webClientResponseException.getResponseBodyAsString(),
                                error
                        );
                    } else {
                        log.error("Feil ved opprettelse av journalpost.", error);
                    }
                });

    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
