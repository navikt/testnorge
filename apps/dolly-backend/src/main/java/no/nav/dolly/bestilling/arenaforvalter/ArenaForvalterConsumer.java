package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterDeleteCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterGetMiljoeCommand;
import no.nav.dolly.config.credentials.ArenaforvalterProxyProperties;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Component
@Slf4j
public class ArenaForvalterConsumer {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private static final String ARENAFORVALTER_DAGPENGER = "/api/v1/dagpenger";

    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final TokenExchange tokenService;

    public ArenaForvalterConsumer(ArenaforvalterProxyProperties serverProperties,
                                  TokenExchange tokenService,
                                  ObjectMapper objectMapper,
                                  ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "arena_getIdent" })
    public ResponseEntity<ArenaArbeidssokerBruker> getIdent(String ident) {

        log.info("Henter bruker på ident: {} fra arena-forvalteren", ident);
        ResponseEntity<ArenaArbeidssokerBruker> response = webClient.get().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .queryParam("filter-personident", ident)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toEntity(ArenaArbeidssokerBruker.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (nonNull(response) && response.hasBody()) {
            log.info("Hentet bruker fra arena: {}", Json.pretty(response.getBody()));
        }
        return response;
    }

    @Timed(name = "providers", tags = { "operation", "arena_deleteIdent" })
    public Mono<List<Void>> deleteIdenter(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call()
                        .map(miljoe -> Flux.range(0, identer.size())
                                .delayElements(Duration.ofMillis(100))
                                .map(index -> new ArenaForvalterDeleteCommand(webClient, identer.get(index), miljoe, token.getTokenValue()).call())))
                .flatMap(Flux::from)
                .flatMap(Flux::from)
                .collectList();
    }

    @Timed(name = "providers", tags = { "operation", "arena_postBruker" })
    public ResponseEntity<ArenaNyeBrukereResponse> postArenadata(ArenaNyeBrukere arenaNyeBrukere) {

        return webClient.post().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(arenaNyeBrukere)
                .retrieve()
                .toEntity(ArenaNyeBrukereResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "arena_postDagpenger" })
    public ResponseEntity<ArenaNyeDagpengerResponse> postArenaDagpenger(ArenaDagpenger arenaDagpenger) {

        return webClient.post().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_DAGPENGER)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(arenaDagpenger)
                .retrieve()
                .toEntity(ArenaNyeDagpengerResponse.class)
                .doOnError(error -> error instanceof WebClientResponseException.InternalServerError,
                        error -> log.error(((WebClientResponseException) error).getResponseBodyAsString(StandardCharsets.UTF_8)))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.InternalServerError,
                        throwable -> Mono.empty())
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "arena_getEnvironments" })
    public List<String> getEnvironments() {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call())
                .collectList()
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    public Map<String, Object> checkStatus() {
        final String TEAM_DOLLY = "Team Dolly";
        //final String TEAM_ARENA = "Team Arena";

        var statusMap =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                WebClient.builder().build());
        statusMap.put("team", TEAM_DOLLY);

//        // "Arena" ikke direkte tilgang
//        var arenaStatusMap =  CheckAliveUtil.checkConsumerStatus(
//                "https://arena-forvalteren.dev.adeo.no/internal/isAlive",
//                "https://arena-forvalteren.dev.adeo.no/internal/isReady",
//                WebClient.builder().build());
//        arenaStatusMap.put("team", TEAM_ARENA);

        return Map.of(
                "testnav-arena-forvalteren-proxy", statusMap
                //"arena-forvalteren", arenaStatusMap
        );
    }
}
