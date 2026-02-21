package no.nav.registre.testnorge.profil.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.config.Consumers;
import no.nav.registre.testnorge.profil.consumer.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavAltinn3TilgangService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<OrganisasjonDTO> getOrganisasjon(String ident, String organisasjonsnummer) {

        return Mono.from(tokenExchange.exchange(serverProperties)
                        .flatMapMany(accessToken ->
                                new GetPersonOrganisasjonTilgangCommand(webClient, ident, accessToken.getTokenValue()).call()))
                .doOnNext(organisasjon -> log.info("Mottatt organisasjon: {}", organisasjon))
                .filter(organisasjon -> organisasjon.getOrganisasjonsnummer().equals(organisasjonsnummer));
    }
}
