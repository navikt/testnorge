package no.nav.dolly.bestilling.inntektstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubDeleteCommand;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubGetCommand;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubPostCommand;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ValiderInntekt;
import no.nav.dolly.config.credentials.InntektstubProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Service
@Slf4j
public class InntektstubConsumer implements ConsumerStatus {

    private static final String VALIDER_INNTEKTER_URL = "/api/v2/valider";

    private static final int BLOCK_SIZE = 10;

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

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

    @Timed(name = "providers", tags = {"operation", "inntk_getInntekter"})
    public Flux<Inntektsinformasjon> getInntekter(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new InntektstubGetCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "inntk_deleteInntekter"})
    public Flux<String> deleteInntekter(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .delayElements(Duration.ofMillis(100))
                        .map(ident -> new InntektstubDeleteCommand(webClient, ident,
                                token.getTokenValue()).call())
                        .flatMap(Flux::from));
    }

    @Timed(name = "providers", tags = {"operation", "inntk_postInntekter"})
    public Flux<Inntektsinformasjon> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {

        log.info("Sender inntektstub: {}", inntektsinformasjon);

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new InntektstubPostCommand(webClient, inntektsinformasjon, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "inntk_validerInntekt"})
    public ResponseEntity<Object> validerInntekter(ValiderInntekt validerInntekt) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(VALIDER_INNTEKTER_URL)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .bodyValue(validerInntekt)
                        .retrieve()
                        .toEntity(Object.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)))
                .block();
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-inntektstub-proxy";
    }
}
