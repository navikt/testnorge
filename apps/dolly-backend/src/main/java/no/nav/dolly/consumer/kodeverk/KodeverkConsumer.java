package no.nav.dolly.consumer.kodeverk;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
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
    private final ServerProperties serverProperties;

    public KodeverkConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getKodeverkApi();
        this.webClient = webClientBuilder
                .exchangeStrategies(
                        ExchangeStrategies
                                .builder()
                                .codecs(configurer -> configurer
                                        .defaultCodecs()
                                        .maxInMemorySize(32 * 1024 * 1024))
                                .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    private static String getNorskBokmaal(Entry<String, java.util.List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().get(0).getBeskrivelser().get("nb").getTekst();
    }

    @Timed(name = "providers", tags = {"operation", "hentKodeverk"})
    public Flux<KodeverkBetydningerResponse> fetchKodeverkByName(String kodeverk) {

        return getKodeverk(kodeverk);
    }

    @Cacheable(CACHE_KODEVERK_2)
    @Timed(name = "providers", tags = {"operation", "hentKodeverk"})
    public Mono<Map<String, String>> getKodeverkByName(String kodeverk) {

        return getKodeverk(kodeverk)
                .map(KodeverkBetydningerResponse::getBetydninger)
                .map(Map::entrySet)
                .flatMap(Flux::fromIterable)
                .filter(entry -> !entry.getValue().isEmpty())
                .filter(entry -> LocalDate.now().isAfter(entry.getValue().get(0).getGyldigFra()) &&
                        LocalDate.now().isBefore(entry.getValue().get(0).getGyldigTil()))
                .collect(Collectors.toMap(Entry::getKey, KodeverkConsumer::getNorskBokmaal))
                .cache(Duration.ofHours(9));
    }

    private Flux<KodeverkBetydningerResponse> getKodeverk(String kodeverk) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path(KODEVERK_URL_BEGINNING)
                                .pathSegment(kodeverk)
                                .pathSegment(KODEVERK_URL_KODER)
                                .pathSegment(KODEVERK_URL_BETYDNINGER)
                                .queryParam("ekskluderUgyldige", true)
                                .queryParam("spraak", "nb")
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, generateCallId())
                        .retrieve()
                        .bodyToFlux(KodeverkBetydningerResponse.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)));
    }
}
