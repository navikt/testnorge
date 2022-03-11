package no.nav.dolly.consumer.kodeverk;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.KodeverkProxyProperties;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK_2;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Component
@Slf4j
public class KodeverkConsumer {

    private static final String KODEVERK_URL_BEGINNING = "/api/v1/kodeverk";
    private static final String KODEVERK_URL_KODER = "koder";
    private static final String KODEVERK_URL_BETYDNINGER = "betydninger";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public KodeverkConsumer(TokenExchange tokenService, KodeverkProxyProperties serverProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(32 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl()).build();
    }

    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public KodeverkBetydningerResponse fetchKodeverkByName(String kodeverk) {

        var kodeverkResponse = getKodeverk(kodeverk);
        return kodeverkResponse.hasBody() ? kodeverkResponse.getBody() : KodeverkBetydningerResponse.builder().build();
    }

    @Cacheable(CACHE_KODEVERK_2)
    @Timed(name = "providers", tags = { "operation", "hentKodeverk" })
    public Map<String, String> getKodeverkByName(String kodeverk) {

        var kodeverkResponse = getKodeverk(kodeverk);
        if (!kodeverkResponse.hasBody()) {
            return Collections.emptyMap();
        }

        return kodeverkResponse.getBody().getBetydninger().entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Entry::getKey, KodeverkConsumer::getNorskBokmaal));
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private ResponseEntity<KodeverkBetydningerResponse> getKodeverk(String kodeverk) {

        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(KODEVERK_URL_BEGINNING)
                            .pathSegment(kodeverk)
                            .pathSegment(KODEVERK_URL_KODER)
                            .pathSegment(KODEVERK_URL_BETYDNINGER)
                            .queryParam("ekskluderUgyldige", true)
                            .queryParam("spraak", "nb")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HEADER_NAV_CALL_ID, generateCallId())
                    .retrieve()
                    .toEntity(KodeverkBetydningerResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

        } catch (WebClientResponseException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }

    private static String getNorskBokmaal(Entry<String, java.util.List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().get(0).getBeskrivelser().get("nb").getTekst();
    }
}
