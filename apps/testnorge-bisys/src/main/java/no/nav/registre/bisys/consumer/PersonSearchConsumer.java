package no.nav.registre.bisys.consumer;

import no.nav.registre.bisys.consumer.command.PersonSearchCommand;
import no.nav.registre.bisys.consumer.credential.PersonSearchProperties;
import no.nav.registre.bisys.consumer.request.PersonSearchRequest;
import no.nav.registre.bisys.consumer.response.PersonSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class PersonSearchConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public PersonSearchConsumer(
            PersonSearchProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public PersonSearchResponse search(PersonSearchRequest request){
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new PersonSearchCommand(request, accessToken.getTokenValue(), webClient).call())
                .block();
    }
}
