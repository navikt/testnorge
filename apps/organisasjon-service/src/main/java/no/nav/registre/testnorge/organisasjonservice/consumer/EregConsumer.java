package no.nav.registre.testnorge.organisasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonservice.config.Consumers;
import no.nav.registre.testnorge.organisasjonservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.organisasjonservice.domain.Organisasjon;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class EregConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public EregConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<Organisasjon> getOrganisasjon(String orgnummer, String miljo) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), miljo, orgnummer).call())
                .map(Organisasjon::new);
    }
}
