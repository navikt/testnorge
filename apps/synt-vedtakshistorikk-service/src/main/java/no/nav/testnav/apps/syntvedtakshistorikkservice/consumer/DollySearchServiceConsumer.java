package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.config.Consumers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.search.PersonSearchCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.search.PersonSearchResponse;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonSearch;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class DollySearchServiceConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public DollySearchServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavDollySearchService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public PersonSearchResponse search(PersonSearch request) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new PersonSearchCommand(request, accessToken.getTokenValue(), webClient).call())
                .block();
    }

}
