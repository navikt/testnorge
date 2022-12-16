package no.nav.dolly.bestilling.sigrunstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sigrunstub.command.SigrunstubDeleteCommand;
import no.nav.dolly.bestilling.sigrunstub.command.SigurunstubPostCommand;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.config.credentials.SigrunstubProxyProperties;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class SigrunStubConsumer implements ConsumerStatus {

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
    public Flux<SigrunstubResponse> deleteSkattegrunnlag(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(50))
                        .map(index -> new SigrunstubDeleteCommand(webClient, identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from));
    }

    @Timed(name = "providers", tags = { "operation", "sigrun_deleteGrunnlag" })
    public Mono<SigrunstubResponse> deleteSkattegrunnlag(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SigrunstubDeleteCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "sigrun_createGrunnlag" })
    public Mono<SigrunstubResponse> createSkattegrunnlag(List<OpprettSkattegrunnlag> request) {

        log.info("Post til Sigrunstub med data {}", request);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SigurunstubPostCommand(webClient, request, token.getTokenValue()).call());
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-sigrunstub-proxy";
    }

}
