package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.Oauth2AuthenticationToken;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Service
@Profile({ "prod", "dev", "idporten" })
@Slf4j
@RequiredArgsConstructor
public class SessionTokenResolver extends Oauth2AuthenticationToken implements TokenResolver {
    private final ObjectProvider<ReactiveOAuth2AuthorizedClientManager> authorizedClientManagerProvider;

    @Override
    public Mono<Token> getToken(ServerWebExchange exchange) {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication).flatMap(authentication -> {
                            if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
                                var manager = authorizedClientManagerProvider.getIfAvailable();
                                if (isNull(manager)) {
                                    return Mono.error(new IllegalStateException("OAuth2 client manager ikke tilgjengelig"));
                                }
                                var authorizeRequest = OAuth2AuthorizeRequest
                                        .withClientRegistrationId(authenticationToken.getAuthorizedClientRegistrationId())
                                        .principal(authenticationToken)
                                        .attribute(ServerWebExchange.class.getName(), exchange)
                                        .build();

                                return manager.authorize(authorizeRequest)
                                        .map(this::toToken)
                                        .switchIfEmpty(Mono.error(new CredentialsExpiredException("Klarte ikke å fornye token")));
                            } else if (authentication instanceof JwtAuthenticationToken) {

                                return getJwtAuthenticationToken()
                                        .map(jwt -> Token.builder()
                                                .clientCredentials(false)
                                                .userId(jwt.getTokenAttributes().get("pid").toString())
                                                .accessTokenValue(jwt.getToken().getTokenValue())
                                                .expiresAt(jwt.getToken().getExpiresAt())
                                                .build());
                            }
                            return Mono.error(new RuntimeException("Klarte ikke å caste authentication til token"));
                        }
                );
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

    private Mono<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(JwtAuthenticationToken.class::cast)
                .doOnError(throwable -> log.error("Klarte ikke hente Jwt Auth Token: ", throwable))
                .handle((jwtAuthenticationToken, sink) -> {
                    Jwt credentials = (Jwt) jwtAuthenticationToken.getCredentials();
                    Instant expiresAt = credentials.getExpiresAt();
                    if (isNull(expiresAt) || expiresAt.isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                        sink.error(new CredentialsExpiredException("Jwt er utløpt eller utløper innen kort tid"));
                    } else {
                        sink.next(jwtAuthenticationToken);
                    }
                });
    }
}
