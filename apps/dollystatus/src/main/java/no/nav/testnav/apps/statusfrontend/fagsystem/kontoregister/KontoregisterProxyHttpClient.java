package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.KontoregisterFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command.CreateKontoregisterAccountCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command.DeleteKontoregisterAccountCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command.GetKontoregisterAccountCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KontoregisterProxyHttpClient implements KontoregisterClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final KontoregisterFunctionalTestProperties properties;
    private final WebClient webClient;

    public KontoregisterProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            KontoregisterFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavDollyProxy();
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<KontoregisterResourceStatus> getAccount(
            RunId runId,
            OppdaterKontoRequestDTO expectedAccount
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetKontoregisterAccountCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedAccount,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createAccount(RunId runId, OppdaterKontoRequestDTO account) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateKontoregisterAccountCommand(
                        webClient,
                        token.getTokenValue(),
                        account,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteAccount(RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteKontoregisterAccountCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        properties.getRequestTimeout()).call());
    }
}
