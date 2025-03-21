package no.nav.testnav.apps.brukerservice.consumer;

import no.nav.testnav.apps.brukerservice.config.Consumers;
import no.nav.testnav.apps.brukerservice.consumer.command.GetBrukertilgangCommand;
import no.nav.testnav.apps.brukerservice.domain.Organisasjon;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AltinnTilgangServiceConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public AltinnTilgangServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient,
            GetAuthenticatedUserId getAuthenticatedUserId
    ) {
        serverProperties = consumers.getTestnavAltinn3TilgangService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.getAuthenticatedUserId = getAuthenticatedUserId;
    }

    public Mono<Organisasjon> getOrganisasjon(String orgnummer) {

        return Mono.from(getAuthenticatedUserId.call()
                .flatMapMany(userId -> tokenExchange.exchange(serverProperties)
                        .flatMapMany(accessToken ->
                                new GetBrukertilgangCommand(webClient, userId, accessToken.getTokenValue()).call()))
                .filter(org -> org.getOrganisasjonsnummer().equals(orgnummer))
                .map(Organisasjon::new));
    }
}
