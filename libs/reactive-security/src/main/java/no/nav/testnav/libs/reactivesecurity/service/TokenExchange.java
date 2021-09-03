package no.nav.testnav.libs.reactivesecurity.service;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;


@Service
@Import({AzureAdTokenExchange.class, TokenXExchange.class})
public class TokenExchange {
    private final AzureAdTokenExchange azureAdTokenExchange;
    private final TokenXExchange tokenXExchange;
    private final AuthenticationTokenResolver tokenResolver;

    public TokenExchange(
            AzureAdTokenExchange azureAdTokenExchange,
            TokenXExchange tokenXExchange,
            AuthenticationTokenResolver tokenResolver
    ) {
        this.azureAdTokenExchange = azureAdTokenExchange;
        this.tokenXExchange = tokenXExchange;
        this.tokenResolver = tokenResolver;
    }

    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return tokenResolver
                .getClientRegistrationId()
                .flatMap(id -> {
                    if (id.equals("idporten")) {
                        return tokenXExchange.generateToken(serverProperties);
                    } else {
                        return azureAdTokenExchange.generateToken(serverProperties);
                    }
                });
    }
}
