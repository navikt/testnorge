package no.nav.dolly.bestilling.instdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.instdata.command.InstdataDeleteCommand;
import no.nav.dolly.bestilling.instdata.command.InstdataGetCommand;
import no.nav.dolly.bestilling.instdata.command.InstdataGetMiljoerCommand;
import no.nav.dolly.bestilling.instdata.command.InstdataPostCommand;
import no.nav.dolly.bestilling.instdata.domain.DeleteResponse;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.bestilling.instdata.domain.InstitusjonsoppholdRespons;
import no.nav.dolly.config.credentials.InstProxyProperties;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class InstdataConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public InstdataConsumer(
            TokenExchange tokenService,
            InstProxyProperties serverProperties,
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

    @Timed(name = "providers", tags = {"operation", "inst_getMiljoer"})
    public Mono<List<String>> getMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new InstdataGetMiljoerCommand(webClient, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "inst_getInstdata"})
    public Mono<InstitusjonsoppholdRespons> getInstdata(String ident, String environment) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new InstdataGetCommand(webClient, ident, environment, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "inst_deleteInstdata"})
    public Mono<List<DeleteResponse>> deleteInstdata(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new InstdataGetMiljoerCommand(webClient, token.getTokenValue()).call()
                        .flatMap(miljoer -> Flux.fromIterable(identer)
                                .flatMap(ident -> new InstdataDeleteCommand(webClient,
                                        ident, miljoer, token.getTokenValue()).call())
                                .collectList()));
    }

    @Timed(name = "providers", tags = {"operation", "inst_postInstdata"})
    public Flux<InstdataResponse> postInstdata(List<Instdata> instdata, String environment) {

        log.info("Instdata opprett til {}: {}", environment, instdata);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(instdata)
                        .flatMap(opphold -> new InstdataPostCommand(webClient, opphold, environment,
                                token.getTokenValue()).call()));
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-inst-proxy";
    }

}
