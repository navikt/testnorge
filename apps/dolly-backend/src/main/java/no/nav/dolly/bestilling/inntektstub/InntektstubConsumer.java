package no.nav.dolly.bestilling.inntektstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubDeleteCommand;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubGetCommand;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubPostCommand;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.config.credentials.InntektstubProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Service
@Slf4j
public class InntektstubConsumer {

    private static final String VALIDER_INNTEKTER_URL = "/api/v2/valider";

    private static final int BLOCK_SIZE = 10;

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public InntektstubConsumer(TokenExchange tokenService,
                               InntektstubProxyProperties serverProperties,
                               ObjectMapper objectMapper,
                               ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_getInntekter" })
    public List<Inntektsinformasjon> getInntekter(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new InntektstubGetCommand(webClient, ident, token.getTokenValue()).call())
                .collectList()
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_deleteInntekter" })
    public Mono<List<String>> deleteInntekter(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, (identer.size() / BLOCK_SIZE) + 1)
                        .delayElements(Duration.ofMillis(100))
                        .map(index -> new InntektstubDeleteCommand(webClient,
                                identer.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, identer.size())),
                                token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_postInntekter" })
    public Flux<Inntektsinformasjon> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {

        log.info("Sender inntektstub: {}", inntektsinformasjon);

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new InntektstubPostCommand(webClient, inntektsinformasjon, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "inntk_validerInntekt" })
    public ResponseEntity<Object> validerInntekter(ValiderInntekt validerInntekt) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(VALIDER_INNTEKTER_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(validerInntekt)
                .retrieve()
                .toEntity(Object.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    public Map<String, Object> checkStatus() {
        final String TEAM_DOLLY = "Team Dolly";
        // final String TEAM_INNTEKT = "Team Inntekt";

        var statusWebClient = WebClient.builder().build();
        var checkMap = new HashMap<String, Object>();

        var statusMap = CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                statusWebClient);
        statusMap.put("team", TEAM_DOLLY);

        checkMap.put("testnav-inntektstub-proxy", statusMap);

//        // "InntektStub" ikke direktre tilgang
//        var inntektStubStatusMap = CheckAliveUtil.checkConsumerStatus(
//                "https://inntektstub.dev.adeo.no/internal/isAlive",
//                "https://inntektstub.dev.adeo.no/internal/isReady",
//                statusWebClient);
//        inntektStubStatusMap.put("team", TEAM_INNTEKT);
//
//        checkMap.put("inntektstub", inntektStubStatusMap);

        return checkMap;
    }
}
