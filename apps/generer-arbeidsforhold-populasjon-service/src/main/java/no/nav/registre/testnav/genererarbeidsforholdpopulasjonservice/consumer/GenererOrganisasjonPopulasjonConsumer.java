package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.config.Consumers;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOpplysningspliktigOrgnummerCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

@Component
public class GenererOrganisasjonPopulasjonConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public GenererOrganisasjonPopulasjonConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavGenererOrganisasjonPopulasjonService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Set<String> getOpplysningspliktig(String miljo) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetOpplysningspliktigOrgnummerCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                miljo
                        ).call()
                ).block();
    }
}
