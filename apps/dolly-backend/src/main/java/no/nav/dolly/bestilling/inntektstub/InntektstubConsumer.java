package no.nav.dolly.bestilling.inntektstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubDeleteCommand;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubGetCommand;
import no.nav.dolly.bestilling.inntektstub.command.InntektstubPostCommand;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.config.credentials.InntektstubProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
@Slf4j
public class InntektstubConsumer implements ConsumerStatus {

    private static final int BLOCK_SIZE = 10;

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public InntektstubConsumer(
            TokenExchange tokenService,
            InntektstubProxyProperties serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "inntk_getInntekter" })
    public Flux<Inntektsinformasjon> getInntekter(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new InntektstubGetCommand(webClient, ident, token.getTokenValue()).call());
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

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-inntektstub-proxy";
    }
}
