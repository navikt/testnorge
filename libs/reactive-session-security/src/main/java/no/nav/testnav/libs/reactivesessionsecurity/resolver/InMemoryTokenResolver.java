package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryTokenResolver extends Oauth2AuthenticationToken implements TokenResolver {
    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public Mono<Token> getToken(ServerWebExchange exchange) {
        return oauth2AuthenticationToken(ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication))
                .flatMap(oAuth2AuthenticationToken -> {
                    var authorizeRequest = OAuth2AuthorizeRequest
                            .withClientRegistrationId(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())
                            .principal(oAuth2AuthenticationToken)
                            .attribute(ServerWebExchange.class.getName(), exchange)
                            .build();

                    return authorizedClientManager.authorize(authorizeRequest)
                            .map(this::toToken)
                            .switchIfEmpty(Mono.error(new CredentialsExpiredException("Klarte ikke å fornye token")));
                });
    }

    private Token toToken(OAuth2AuthorizedClient authorizedClient) {
        var builder = Token.builder()
                .accessTokenValue(authorizedClient.getAccessToken().getTokenValue())
                .expiresAt(authorizedClient.getAccessToken().getExpiresAt())
                .clientCredentials(false);

        if (nonNull(authorizedClient.getRefreshToken())) {
            builder.refreshTokenValue(authorizedClient.getRefreshToken().getTokenValue());
        }

        return builder.build();
    }
}