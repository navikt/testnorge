package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.PersonSearchCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PersonSearchServiceProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.personSearch.PersonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PersonSearchServiceConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public PersonSearchServiceConsumer(
            PersonSearchServiceProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public PersonDTO search(PersonSearchRequest request){
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new PersonSearchCommand(request, accessToken.getTokenValue(), webClient).call())
                .block();
    }
}
