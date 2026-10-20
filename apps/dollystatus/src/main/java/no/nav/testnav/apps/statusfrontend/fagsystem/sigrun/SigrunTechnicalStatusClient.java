package no.nav.testnav.apps.statusfrontend.fagsystem.sigrun;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SigrunTechnicalStatusProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.sigrun.command.GetSigrunReadinessCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SigrunTechnicalStatusClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final SigrunTechnicalStatusProperties properties;
    private final WebClient webClient;

    public SigrunTechnicalStatusClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            SigrunTechnicalStatusProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavDollyProxy();
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Void> checkReadiness() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetSigrunReadinessCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getRequestTimeout()).call());
    }
}
