package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.GetWellKnownCommand;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.WellKnown;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@Import({
        AzureNavClientCredential.class
})
public class AzureAdTokenExchange implements ExchangeToken {
    private final WebClient webClient;
    private final TokenResolver tokenResolver;
    private final ClientCredential clientCredential;
    private final Mono<WellKnown> wellKnown;

    public AzureAdTokenExchange(
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            TokenResolver tokenResolver,
            AzureNavClientCredential clientCredential
    ) {
        this.webClient = WebClient
                .builder()
                .build();
        this.tokenResolver = tokenResolver;
        this.clientCredential = clientCredential;
        this.wellKnown = new GetWellKnownCommand(this.webClient, issuerUrl + "/v2.0").call().cache(
                value -> Duration.ofDays(7),
                value -> Duration.ZERO,
                () -> Duration.ZERO
        );
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {
        return tokenResolver
                .getToken(exchange)
                .flatMap(token -> new OnBehalfOfExchangeCommand(
                        webClient,
                        clientCredential,
                        serverProperties.toAzureAdScope(),
                        token,
                        wellKnown
                ).call());
    }

    public Mono<AccessToken> generateClientCredentialAccessToken(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(webClient, clientCredential, serverProperties.toAzureAdScope()).call();
    }

}
