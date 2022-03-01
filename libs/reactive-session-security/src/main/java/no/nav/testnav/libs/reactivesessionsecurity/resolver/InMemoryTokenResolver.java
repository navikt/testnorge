package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
                                oAuth2AuthenticationToken.getPrincipal().getName())
                        .map(oAuth2AuthorizedClient -> {
                                    if (oAuth2AuthorizedClient.getAccessToken().getExpiresAt().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(180))) {
                                        log.warn("Auth client har utløpt, fjerner den som authenticated");
                                        oAuth2AuthenticationToken.setAuthenticated(false);
                                        oAuth2AuthenticationToken.eraseCredentials();
                                    }
                                    return Token.builder()
                                            .accessTokenValue(oAuth2AuthorizedClient.getAccessToken().getTokenValue())
                                            .expiresAt(oAuth2AuthorizedClient.getAccessToken().getExpiresAt())
                                            .refreshTokenValue(nonNull(oAuth2AuthorizedClient.getRefreshToken()) ? oAuth2AuthorizedClient.getRefreshToken().getTokenValue() : null)
                                            .clientCredentials(false)
                                            .build();
                                }
                        ));
    }
}