package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.command.tokenx.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Import({
        TokenXProperties.class
})
@RequiredArgsConstructor
public class TokenXExchange implements ExchangeToken {

    private final TokenResolver tokenService;
    private final WebClient webClient;
    private final TokenXProperties tokenX;

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {
        return tokenService
                .getToken(exchange)
                .flatMap(token -> new OnBehalfOfExchangeCommand(
                        webClient,
                        tokenX,
                        serverProperties.toTokenXScope(),
                        token.getAccessTokenValue()
                ).call());
    }

}