package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOpplysningspliktigOrgnummerCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.GenererOrganisasjonPopulasjonServerProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class GenererOrganisasjonPopulasjonConsumer {
    private final WebClient webClient;
    private final ServerProperties properties;
    private final TokenExchange tokenExchange;

    public GenererOrganisasjonPopulasjonConsumer(
            GenererOrganisasjonPopulasjonServerProperties properties,
            TokenExchange tokenExchange
    ) {
        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Set<String> getOpplysningspliktig(String miljo) {
        return tokenExchange.generateToken(properties)
                .flatMap(accessToken -> new GetOpplysningspliktigOrgnummerCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                miljo
                        ).call()
                ).block();
    }
}
