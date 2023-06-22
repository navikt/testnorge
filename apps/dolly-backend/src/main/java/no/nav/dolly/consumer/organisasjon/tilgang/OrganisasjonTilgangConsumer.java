package no.nav.dolly.consumer.organisasjon.tilgang;

import no.nav.dolly.config.credentials.OrganisasjonTilgangProperties;
import no.nav.dolly.consumer.organisasjon.tilgang.command.OrganisasjonTilgangGetCommand;
import no.nav.dolly.consumer.organisasjon.tilgang.dto.OrganisasjonTilgang;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class OrganisasjonTilgangConsumer {

    private WebClient webClient;
    private ServerProperties serverProperties;
    private TokenExchange tokenExchange;

    public OrganisasjonTilgangConsumer(
            TokenExchange tokenExchange,
            OrganisasjonTilgangProperties serverProperties,
            WebClient.Builder webClientBuilder) {

        this.tokenExchange = tokenExchange;
        this.serverProperties = serverProperties;
        this.webClient = webClientBuilder
                .exchangeStrategies(
                        ExchangeStrategies
                                .builder()
                                .codecs(configurer -> configurer
                                        .defaultCodecs()
                                        .maxInMemorySize(32 * 1024 * 1024))
                                .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<OrganisasjonTilgang> getOrgansisjonTilganger() {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new OrganisasjonTilgangGetCommand(webClient, token.getTokenValue()).call());
    }
}
