package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.tpsmessagingservice.command.MiljoerGetCommand;
import no.nav.dolly.config.credentials.TpsMiljoerProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class TpsMiljoerConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public TpsMiljoerConsumer(TokenExchange tokenService,
                              TpsMiljoerProperties serverProperties,
                              ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<AccessToken> getToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "get_tps_miljoer"})
    public Mono<List<String>> getTpsMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new MiljoerGetCommand(webClient, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-miljoer-consumer";
    }
}
