package no.nav.testnav.libs.reactivesecurity.exchange.tokenx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.tokenx.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
public class TokenXService implements TokenService {
    private final GetAuthenticatedToken getAuthenticatedTokenAction;
    private final WebClient webClient;
    private final TokenXProperties tokenX;

    TokenXService(TokenXProperties tokenX, GetAuthenticatedToken tokenResolver) {
        log.info("Init TokenX token exchange.");
        this.webClient = WebClient.builder().build();
        this.tokenX = tokenX;
        this.getAuthenticatedTokenAction = tokenResolver;
    }

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return getAuthenticatedTokenAction.call()
                .flatMap(token -> new OnBehalfOfExchangeCommand(webClient, tokenX, serverProperties.toTokenXScope(), token).call());
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }
}
