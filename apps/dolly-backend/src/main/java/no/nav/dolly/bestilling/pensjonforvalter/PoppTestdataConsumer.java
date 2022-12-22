package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetPoppInntekterCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetPoppMiljoerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagrePoppInntektCommand;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagrePoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.config.credentials.PoppTestdataProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PoppTestdataConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PoppTestdataConsumer(TokenExchange tokenService,
                                PoppTestdataProperties serverProperties,
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

    public Mono<AccessToken> getAccessToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "popp_getMiljoer"})
    public Set<String> getMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetPoppMiljoerCommand(webClient, token.getTokenValue()).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "popp_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetPoppInntekterCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "popp_lagreInntekt"})
    public Flux<PensjonforvalterResponse> lagreInntekt(LagrePoppInntektRequest lagreInntektRequest, AccessToken token, String miljoe) {

        log.info("Popp lagre inntekt {}", lagreInntektRequest);
        return new LagrePoppInntektCommand(webClient, token.getTokenValue(), lagreInntektRequest, miljoe).call();
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-popp-testdata-proxy";
    }
}
