package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.securitycore.domain.Token;


@Service
@RequiredArgsConstructor
public class RedisTokenResolver extends Oauth2AuthenticationToken implements TokenResolver {
    private final ServerOAuth2AuthorizedClientRepository clientRepository;

    @Override
    public Mono<Token> getToken(ServerWebExchange exchange) {
        return oauth2AuthenticationToken()
                .flatMap(authenticationToken -> clientRepository.loadAuthorizedClient(
                                authenticationToken.getAuthorizedClientRegistrationId(),
                                authenticationToken,
                                exchange
                        ).map(OAuth2AuthorizedClient::getAccessToken)
                ).map(accessToken -> Token.builder().value(accessToken.getTokenValue()).clientCredentials(false).build());
    }
}
