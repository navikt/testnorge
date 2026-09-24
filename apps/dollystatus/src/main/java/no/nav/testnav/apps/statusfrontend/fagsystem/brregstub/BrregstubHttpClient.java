package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub;

import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.BrregstubFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.CreateBrregstubRoleOverviewCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.DeleteBrregstubOrganizationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.DeleteBrregstubRoleOverviewCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.GetBrregstubOrganizationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command.GetBrregstubRoleOverviewCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BrregstubHttpClient implements BrregstubClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final BrregstubFunctionalTestProperties properties;
    private final WebClient webClient;

    public BrregstubHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            BrregstubFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavDollyProxy();
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<BrregstubResourceStatus> getRoleOverview(BrregstubRequest expectedRequest) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetBrregstubRoleOverviewCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest.fnr(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<BrregstubResourceStatus> getOrganization(BrregstubRequest expectedRequest) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetBrregstubOrganizationCommand(
                        webClient,
                        token.getTokenValue(),
                        BrregstubTestData.ORGANIZATION_NUMBER,
                        expectedRequest.fnr(),
                        expectedRequest.enheter().getFirst().registreringsdato(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createRoleOverview(BrregstubRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateBrregstubRoleOverviewCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteRoleOverview(String ident) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteBrregstubRoleOverviewCommand(
                        webClient,
                        token.getTokenValue(),
                        ident,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteOrganization() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteBrregstubOrganizationCommand(
                        webClient,
                        token.getTokenValue(),
                        BrregstubTestData.ORGANIZATION_NUMBER,
                        properties.getRequestTimeout()).call());
    }
}
