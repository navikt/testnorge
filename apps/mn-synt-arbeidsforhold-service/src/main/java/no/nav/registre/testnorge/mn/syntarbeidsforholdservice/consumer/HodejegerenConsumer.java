package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials.HodejegerenProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command.GetLevendeIdenterCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class HodejegerenConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public HodejegerenConsumer(
            @Value("${consumers.hodejegeren.url}") String url,
            HodejegerenProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Flux<String> getIdenter(String miljo, int antall) {
        return tokenExchange.exchange(serviceProperties)
                .flatMapMany(accessToken -> new GetLevendeIdenterCommand(webClient, miljo, antall, accessToken.getTokenValue()).call());
    }
}
