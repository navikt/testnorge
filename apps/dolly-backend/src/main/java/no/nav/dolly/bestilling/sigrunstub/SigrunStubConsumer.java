package no.nav.dolly.bestilling.sigrunstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.command.SigrunstubSlettCommand;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import no.nav.dolly.config.credentials.SigrunstubProxyProperties;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class SigrunStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String URL_VERSION = "/api/v1";
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = URL_VERSION + "/lignetinntekt";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public SigrunStubConsumer(TokenExchange tokenService,
                              SigrunstubProxyProperties serverProperties,
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

    @Timed(name = "providers", tags = { "operation", "sigrun_deleteGrunnlag" })
    public Mono<List<String>> deleteSkattegrunnlag(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .map(index -> new SigrunstubSlettCommand(webClient, identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = { "operation", "sigrun_createGrunnlag" })
    public ResponseEntity<SigrunResponse> createSkattegrunnlag(List<OpprettSkattegrunnlag> request) {

        var callId = getNavCallId();
        log.info("Post til sigrun-stub med call-id {} og data {}", callId, request);

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SIGRUN_STUB_OPPRETT_GRUNNLAG)
                        .build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(request)
                .retrieve()
                .toEntity(SigrunResponse.class)
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

    public Map<String, Object> checkStatus() {
        final String TEAM_DOLLY = "Team Dolly";
        final String TEAM_INNTEKT = "Team Inntekt";

        var statusMap =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                WebClient.builder().build());
        statusMap.put("team", TEAM_DOLLY);

        // "SigrunStub" ikke direkte tilgang
        var sigrunStubStatus = CheckAliveUtil.checkConsumerStatus(
                "https://sigrun-skd-stub.dev.adeo.no/internal/isAlive",
                "https://sigrun-skd-stub.dev.adeo.no/internal/isAlive", // samme url brukes for begge
                WebClient.builder().build());
        sigrunStubStatus.put("team", TEAM_INNTEKT);

        return Map.of(
                "testnav-sigrunstub-proxy", statusMap,
                "sigrun-skd-stub", sigrunStubStatus
        );
    }
}
