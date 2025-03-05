package no.nav.testnav.libs.reactivesecurity.exchange.tokenx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.tokenx.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
@RequiredArgsConstructor
public class TokenXService implements TokenService {

    private final GetAuthenticatedToken getAuthenticatedTokenAction;
    private final WebClient webClient;
    private final TokenXProperties tokenX;

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedTokenAction.call()
                .flatMap(token -> new OnBehalfOfExchangeCommand(webClient, tokenX, serverProperties.toTokenXScope(), token.getAccessTokenValue()).call());
    }

    public Mono<AccessToken> exchange(ServerProperties serverProperties, String faketoken) {
        return new OnBehalfOfExchangeCommand(webClient, tokenX, serverProperties.toTokenXScope(), faketoken).call();
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }

}
