package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command.VerifyPdlPersonCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PdlProxyHttpClient implements PdlProxyClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties properties;
    private final WebClient webClient;

    public PdlProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavPdlProxy();
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<Boolean> personExists(FunctionalTestEnvironment environment, RunId runId) {
        if (environment != FunctionalTestEnvironment.Q1
                && environment != FunctionalTestEnvironment.Q2) {
            return Mono.error(new IllegalArgumentException("PDL-miljøet støttes ikke."));
        }
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new VerifyPdlPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getIdent(),
                        environment,
                        runId,
                        properties.getRequestTimeout()).call());
    }
}
