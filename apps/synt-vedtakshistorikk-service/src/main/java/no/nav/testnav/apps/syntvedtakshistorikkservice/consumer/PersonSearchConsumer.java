package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.search.PersonSearchCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PersonSearchProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.PersonSearchResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
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

    public PersonSearchResponse search(PersonSearchRequest request) {
        try {
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new PersonSearchCommand(request, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Feil oppsto i henting av søkeresultat.", e);
            return null;
        }
    }
}
