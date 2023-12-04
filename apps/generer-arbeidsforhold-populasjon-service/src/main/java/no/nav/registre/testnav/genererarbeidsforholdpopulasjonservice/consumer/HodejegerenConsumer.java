package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.config.Consumers;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetLevendeIdenterCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class HodejegerenConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public HodejegerenConsumer(
            TokenExchange tokenExchange,
            Consumers consumers) {

        serverProperties = consumers.getTestnavHodejegerenProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<String> getIdenter(String miljo, int antall) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        return new GetLevendeIdenterCommand(webClient, miljo, antall, accessToken.getTokenValue()).call();
    }
}
