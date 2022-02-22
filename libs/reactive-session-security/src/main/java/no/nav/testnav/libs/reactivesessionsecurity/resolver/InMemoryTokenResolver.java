package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryTokenResolver extends Oauth2AuthenticationToken implements TokenResolver {

    private final ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService;

    @Override
    public Mono<Token> getToken(ServerWebExchange exchange) {
        return oauth2AuthenticationToken()
                .flatMap(oAuth2AuthenticationToken -> auth2AuthorizedClientService.loadAuthorizedClient(
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                        oAuth2AuthenticationToken.getPrincipal().getName()
                ).map(client -> Token
                        .builder()
                        .accessTokenValue(client.getAccessToken().getTokenValue())
                        .expiresAt(client.getAccessToken().getExpiresAt())
                        .refreshTokenValue(nonNull(client.getRefreshToken()) ? client.getRefreshToken().getTokenValue() : null)
                        .clientCredentials(false)
                        .build()));
    }
}
