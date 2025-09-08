package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.Oauth2AuthenticationToken;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.util.Objects.nonNull;


@Service
@Profile({ "prod", "dev", "idporten" })
@Slf4j
@RequiredArgsConstructor
public class SessionTokenResolver extends Oauth2AuthenticationToken implements TokenResolver {
    private final ServerOAuth2AuthorizedClientRepository clientRepository;

    @Override
    public Mono<Token> getToken(ServerWebExchange exchange) {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication).flatMap(authentication -> {
                            if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
                                return clientRepository.loadAuthorizedClient(
                                                authenticationToken.getAuthorizedClientRegistrationId(),
                                                authenticationToken,
                                                exchange)
                                        .publishOn(Schedulers.boundedElastic())
                                        .mapNotNull(oAuth2AuthorizedClient -> {
                                            if (oAuth2AuthorizedClient.getAccessToken().getExpiresAt().isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                                                log.warn("Auth client har utløpt, fjerner den som authenticated");
                                                authenticationToken.setAuthenticated(false);
                                                authenticationToken.eraseCredentials();
                                                clientRepository.removeAuthorizedClient(
                                                        oAuth2AuthorizedClient.getClientRegistration().getRegistrationId(),
                                                        authenticationToken,
                                                        exchange).block();
                                                return null;
                                            }
                                            return Token.builder()
                                                    .accessTokenValue(oAuth2AuthorizedClient.getAccessToken().getTokenValue())
                                                    .expiresAt(oAuth2AuthorizedClient.getAccessToken().getExpiresAt())
                                                    .refreshTokenValue(nonNull(oAuth2AuthorizedClient.getRefreshToken()) ? oAuth2AuthorizedClient.getRefreshToken().getTokenValue() : null)
                                                    .clientCredentials(false)
                                                    .build();
                                        });
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

    private Mono<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(JwtAuthenticationToken.class::cast)
                .doOnError(throwable -> log.error("Klarte ikke hente Jwt Auth Token: ", throwable))
                .doOnSuccess(jwtAuthenticationToken -> {
                    Jwt credentials = (Jwt) jwtAuthenticationToken.getCredentials();
                    Instant expiresAt = credentials.getExpiresAt();
                    if (expiresAt == null || expiresAt.isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                        throw new CredentialsExpiredException("Jwt er utløpt eller utløper innen kort tid");
                    }
                });
    }
}
