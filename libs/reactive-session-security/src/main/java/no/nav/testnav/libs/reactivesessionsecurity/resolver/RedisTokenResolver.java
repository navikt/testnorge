package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Service
@Slf4j
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
                ).map(accessToken -> {
                    log.info("Hentet token fra RedisResolver");
                    return Token.builder()
                            .value(accessToken.getTokenValue())
                            .expiredAt(accessToken.getExpiresAt())
                            .clientCredentials(false)
                            .build();
                });
    }
}
