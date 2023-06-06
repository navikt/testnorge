package no.nav.dolly.bestilling.medl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.medl.command.MedlPostCommand;
import no.nav.dolly.bestilling.medl.command.MedlPutCommand;
import no.nav.dolly.bestilling.medl.command.getMedldataCommand;
import no.nav.dolly.bestilling.medl.dto.MedlPostResponse;
import no.nav.dolly.config.credentials.MedlProxyProperties;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.dolly.domain.resultset.medl.MedlDataResponse;
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

@Slf4j
@Service
public class MedlConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public MedlConsumer(
            TokenExchange tokenService,
            MedlProxyProperties serverProperties,
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

    @Timed(name = "providers", tags = { "operation", "medl_createMedlemskapsperiode" })
    public Mono<MedlPostResponse> createMedlemskapsperiode(MedlData medlData) {

        log.info("Medlemskapsperiode opprett {}", medlData);
        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new MedlPostCommand(webClient, medlData, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "medl_deleteMedlemskapsperioder" })
    public Flux<MedlPostResponse> deleteMedlemskapsperioder(Flux<MedlData> medlDataRequests) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token ->
                        medlDataRequests.flatMap(medlData ->
                                new MedlPutCommand(webClient, medlData, token.getTokenValue()).call()));
    }

    @Timed(name = "providers", tags = { "operation", "medl_getMedlemskapsperiode" })
    public Flux<MedlDataResponse> getMedlemskapsperioder(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(100))
                        .flatMap(idx -> new getMedldataCommand(webClient, identer.get(idx),
                                token.getTokenValue()).call()));
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-medl-proxy";
    }

}
