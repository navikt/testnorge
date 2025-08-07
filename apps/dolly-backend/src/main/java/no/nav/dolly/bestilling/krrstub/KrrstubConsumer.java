package no.nav.dolly.bestilling.krrstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.krrstub.command.KontaktadataDeleteCommand;
import no.nav.dolly.bestilling.krrstub.command.KontaktdataPostCommand;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.service.CheckAliveService;
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
public class KrrstubConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public KrrstubConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient,
            CheckAliveService checkAliveService    ) {

        super(checkAliveService);
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavKrrstubProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "krrstub_createKontaktdata"})
    public Mono<DigitalKontaktdataResponse> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        log.info("Kontaktdata opprett {}", digitalKontaktdata);
        return tokenService.exchange(serverProperties)
                .flatMap(token -> new KontaktdataPostCommand(webClient, digitalKontaktdata, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "krrstub_deleteKontaktdata"})
    public Flux<DigitalKontaktdataResponse> deleteKontaktdata(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(100))
                        .flatMap(idx -> new KontaktadataDeleteCommand(webClient, identer.get(idx),
                                token.getTokenValue()).call()));
    }

    public Mono<DigitalKontaktdataResponse> deleteKontaktdataPerson(String ident) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new KontaktadataDeleteCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-krrstub-proxy";
    }

}
