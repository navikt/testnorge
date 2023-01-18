package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetBrukerCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetTokenCommand;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;
import no.nav.testnav.apps.oversiktfrontend.credentials.TestnavBrukerServiceProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BrukerConsumer {
    private final WebClient webClient;
    private final TestnavBrukerServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public BrukerConsumer(TestnavBrukerServiceProperties serviceProperties,
                          TokenExchange tokenExchange,
                          ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<BrukerDTO> getBruker(String orgnummer) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetBrukerCommand(webClient, accessToken.getTokenValue(), orgnummer).call());
    }

    public Mono<String> getToken(String id) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetTokenCommand(webClient, accessToken.getTokenValue(), id).call());
    }

}
