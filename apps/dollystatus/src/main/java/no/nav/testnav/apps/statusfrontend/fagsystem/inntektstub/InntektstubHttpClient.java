package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.InntektstubFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command.CreateInntektstubIncomeCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command.DeleteInntektstubIncomeCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command.GetInntektstubIncomeCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InntektstubHttpClient implements InntektstubClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final InntektstubFunctionalTestProperties properties;
    private final WebClient webClient;

    public InntektstubHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            InntektstubFunctionalTestProperties properties,
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
    public Mono<InntektstubResourceStatus> getIncome(InntektstubRequest expectedRequest) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetInntektstubIncomeCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createIncome(InntektstubRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateInntektstubIncomeCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteIncome(String ident) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteInntektstubIncomeCommand(
                        webClient,
                        token.getTokenValue(),
                        ident,
                        properties.getRequestTimeout()).call());
    }
}
