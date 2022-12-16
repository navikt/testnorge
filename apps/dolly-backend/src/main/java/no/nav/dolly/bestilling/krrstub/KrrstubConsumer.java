package no.nav.dolly.bestilling.krrstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.krrstub.command.KontaktadataDeleteCommand;
import no.nav.dolly.bestilling.krrstub.command.KontaktdataPostCommand;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KrrstubConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public KrrstubConsumer(TokenExchange tokenService,
                           KrrstubProxyProperties serverProperties,
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

    @Timed(name = "providers", tags = {"operation", "krrstub_createKontaktdata"})
    public Mono<DigitalKontaktdataResponse> createDigitalKontaktdata(DigitalKontaktdata digitalKontaktdata) {

        log.info("Kontaktdata opprett {}", digitalKontaktdata);
        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new KontaktdataPostCommand(webClient, digitalKontaktdata, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "krrstub_getKontaktdata" })
    public Flux<DigitalKontaktdataResponse> deleteKontaktdata(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(100))
                        .flatMap(idx -> new KontaktadataDeleteCommand(webClient, identer.get(idx),
                                token.getTokenValue()).call()));
    }

    public Mono<DigitalKontaktdataResponse> deleteKontaktdataPerson(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new KontaktadataDeleteCommand(webClient, ident, token.getTokenValue()).call());
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
        return "testnav-krrstub-proxy";
    }

}
