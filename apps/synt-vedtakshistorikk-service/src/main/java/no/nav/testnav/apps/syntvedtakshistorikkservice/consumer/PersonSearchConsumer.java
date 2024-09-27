package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.config.Consumers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.search.PersonSearchCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.search.PersonSearchResponse;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class PersonSearchConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PersonSearchConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavPersonSearchService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public PersonSearchResponse search(PersonSearch request) {
        try {
            return tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new PersonSearchCommand(request, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Feil oppsto i henting av søkeresultat.", e);
            return null;
        }
    }
}
