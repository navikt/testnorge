package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.texas.TexasClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenExchange {

    private final ClientRegistrationIdResolver clientRegistrationIdResolver;
    private final TokenResolver tokenResolver;
    private final TexasClient texasClient;

    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {
        return clientRegistrationIdResolver.getClientRegistrationId()
                .flatMap(type -> tokenResolver.getToken(exchange)
                        .flatMap(token -> switch (type) {
                            case AZURE_AD -> texasClient.exchange(TexasClient.ENTRA_ID, serverProperties.toAzureAdScope(), token.getAccessTokenValue());
                            case TOKEN_X -> texasClient.exchange(TexasClient.TOKENX, serverProperties.toTokenXScope(), token.getAccessTokenValue());
                        }));
    }
}
