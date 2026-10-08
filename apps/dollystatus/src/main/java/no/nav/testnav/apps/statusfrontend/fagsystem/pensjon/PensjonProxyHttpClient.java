package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PensjonFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreateAfpOffentligCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreatePensjonsavtaleCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreatePoppCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.CreateTpForholdCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeleteAfpOffentligCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeletePensjonsavtaleCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeletePoppCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.DeleteTpForholdCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetAfpOffentligCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetPensjonsavtaleCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetPoppCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pensjon.command.GetTpForholdCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PensjonProxyHttpClient implements PensjonClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final PensjonFunctionalTestProperties properties;
    private final WebClient webClient;

    public PensjonProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            PensjonFunctionalTestProperties properties,
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
    public Mono<Void> createTpForhold(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateTpForholdCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        PensjonTestData.tpRequest(pdlProperties.getIdent(), environment),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<PensjonResourceStatus> getTpForhold(
            FunctionalTestEnvironment environment,
            RunId runId
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetTpForholdCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        PensjonTestData.TP_ORDNING,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteTpForhold(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteTpForholdCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createPopp(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreatePoppCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        PensjonTestData.poppRequest(pdlProperties.getIdent(), environment),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<PensjonResourceStatus> getPopp(
            FunctionalTestEnvironment environment,
            RunId runId
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetPoppCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        PensjonTestData.POPP_YEAR,
                        PensjonTestData.POPP_AMOUNT,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deletePopp(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeletePoppCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createAfpOffentlig(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateAfpOffentligCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        PensjonTestData.afpRequest(pdlProperties.getIdent()),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<PensjonResourceStatus> getAfpOffentlig(
            FunctionalTestEnvironment environment,
            RunId runId
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetAfpOffentligCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        PensjonTestData.AFP_TP_ID,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteAfpOffentlig(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteAfpOffentligCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createPensjonsavtale(RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreatePensjonsavtaleCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        PensjonTestData.pensjonsavtaleRequest(pdlProperties.getIdent()),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<PensjonResourceStatus> getPensjonsavtale(
            FunctionalTestEnvironment environment,
            RunId runId
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetPensjonsavtaleCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        PensjonTestData.environmentName(environment),
                        PensjonTestData.PENSJONSAVTALE_PRODUCT,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deletePensjonsavtale(RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeletePensjonsavtaleCommand(
                        webClient,
                        token.getTokenValue(),
                        runId,
                        pdlProperties.getIdent(),
                        properties.getRequestTimeout()).call());
    }
}
