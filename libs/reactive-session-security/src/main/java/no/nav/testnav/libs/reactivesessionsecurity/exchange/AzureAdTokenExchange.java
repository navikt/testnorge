package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AzureAdTokenExchange implements ExchangeToken {
    private final WebClient webClient;
    private final TokenResolver tokenResolver;
    private final ClientCredential clientCredential;

    AzureAdTokenExchange(
            TokenResolver tokenResolver,
            AzureClientCredential clientCredential) {

        this.webClient = WebClient
                .builder()
                .baseUrl(clientCredential.getTokenEndpoint())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.tokenResolver = tokenResolver;
        this.clientCredential = clientCredential;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {
        return tokenResolver
                .getToken(exchange)
                .flatMap(token -> new OnBehalfOfExchangeCommand(
                        webClient,
                        clientCredential,
                        serverProperties.toAzureAdScope(),
                        token
                ).call());
    }
}
