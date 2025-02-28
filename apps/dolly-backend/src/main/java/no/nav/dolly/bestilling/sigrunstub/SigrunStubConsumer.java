package no.nav.dolly.bestilling.sigrunstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sigrunstub.command.SigrunstubLignetDeleteCommand;
import no.nav.dolly.bestilling.sigrunstub.command.SigrunstubPensjonsgivendeDeleteCommand;
import no.nav.dolly.bestilling.sigrunstub.command.SigurunstubPutCommand;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubLignetInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class SigrunStubConsumer extends ConsumerStatus {

    private static final String SIGRUN_STUB_LIGNET_INNTEKT_URL = "/api/v1/lignetinntekt";
    private static final String SIGRUN_STUB_PENSJONSGIVENDE_INNTEKT_URL = "/api/v1/pensjonsgivendeinntektforfolketrygden";
    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public SigrunStubConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavSigrunstubProxy();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "sigrun_deleteGrunnlag"})
    public Flux<SigrunstubResponse> deleteLignetInntekt(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(50))
                        .map(index -> new SigrunstubLignetDeleteCommand(webClient, identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from));
    }

    @Timed(name = "providers", tags = {"operation", "sigrun_deleteGrunnlag"})
    public Flux<SigrunstubResponse> deletePensjonsgivendeInntekt(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(50))
                        .map(index -> new SigrunstubPensjonsgivendeDeleteCommand(webClient, identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from));
    }

    @Timed(name = "providers", tags = {"operation", "sigrun_createGrunnlag"})
    public Mono<SigrunstubResponse> updateLignetInntekt(List<SigrunstubLignetInntektRequest> request) {

        log.info("Put lignet inntekt til Sigrunstub med data {}", request);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SigurunstubPutCommand(webClient, SIGRUN_STUB_LIGNET_INNTEKT_URL, request, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "sigrun_createPensjonsgivendeInntekt"})
    public Mono<SigrunstubResponse> updatePensjonsgivendeInntekt(List<SigrunstubPensjonsgivendeInntektRequest> request) {

        log.info("Put pensjonsgivende inntekt til Sigrunstub med data {}", request);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SigurunstubPutCommand(webClient, SIGRUN_STUB_PENSJONSGIVENDE_INNTEKT_URL,
                        request, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-sigrunstub-proxy";
    }

}
