package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;


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
                ).mapNotNull(oAuth2AuthorizedClient -> {
                    log.info("Henter token fra Redis som utløper: {}", oAuth2AuthorizedClient.getAccessToken().getExpiresAt());
//                    if (oAuth2AuthorizedClient.getAccessToken().getExpiresAt().isBefore(ZonedDateTime.now().toInstant().plusSeconds(180))) {
                        if (oAuth2AuthorizedClient.getAccessToken().getExpiresAt().isAfter(oAuth2AuthorizedClient.getAccessToken().getExpiresAt().plusSeconds(60))) {
                            log.warn("Auth client har utløpt, fjerner den som authenticated");
                            authenticationToken.setAuthenticated(false);
                            authenticationToken.eraseCredentials();
                            return null;
                        }
                    return Token.builder()
                            .accessTokenValue(oAuth2AuthorizedClient.getAccessToken().getTokenValue())
                            .expiresAt(oAuth2AuthorizedClient.getAccessToken().getExpiresAt())
                            .refreshTokenValue(nonNull(oAuth2AuthorizedClient.getRefreshToken()) ? oAuth2AuthorizedClient.getRefreshToken().getTokenValue() : null)
                            .clientCredentials(false)
                            .build();
                }));
    }
}
