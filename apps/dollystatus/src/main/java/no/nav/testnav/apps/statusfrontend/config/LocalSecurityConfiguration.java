package no.nav.testnav.apps.statusfrontend.config;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Profile("local")
@EnableWebFluxSecurity
class LocalSecurityConfiguration {

    @Bean
    SecurityWebFilterChain localSecurityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchange -> authorizeExchange
                        .pathMatchers("/internal/**", "/oauth2/**", "/login/oauth2/**")
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Primary
    GetAuthenticatedResourceServerType localAuthenticatedResourceServerType() {
        return new LocalAuthenticatedResourceServerType();
    }

    @Bean
    @Primary
    GetAuthenticatedUserId localAuthenticatedUserId(
            GetAuthenticatedResourceServerType authenticatedResourceServerType
    ) {
        return new LocalAuthenticatedUserId(authenticatedResourceServerType);
    }

    @Bean
    @Primary
    GetAuthenticatedToken localAuthenticatedToken(
            GetAuthenticatedResourceServerType authenticatedResourceServerType,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        return new LocalAuthenticatedToken(authenticatedResourceServerType, authorizedClientService);
    }
}

final class LocalAuthenticatedResourceServerType extends GetAuthenticatedResourceServerType {

    private static final String AZURE_REGISTRATION_ID = "aad";

    LocalAuthenticatedResourceServerType() {
        super(List.of());
    }

    @Override
    public Mono<ResourceServerType> call() {
        return LocalAuthentication.current()
                .filter(authentication ->
                        AZURE_REGISTRATION_ID.equals(authentication.getAuthorizedClientRegistrationId()))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Lokal Azure AD-innlogging mangler.")))
                .thenReturn(ResourceServerType.AZURE_AD);
    }
}

final class LocalAuthenticatedUserId extends GetAuthenticatedUserId {

    LocalAuthenticatedUserId(GetAuthenticatedResourceServerType authenticatedResourceServerType) {
        super(authenticatedResourceServerType);
    }

    @Override
    public Mono<String> call() {
        return LocalAuthentication.current()
                .map(OAuth2AuthenticationToken::getName);
    }
}

final class LocalAuthenticatedToken extends GetAuthenticatedToken {

    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    LocalAuthenticatedToken(
            GetAuthenticatedResourceServerType authenticatedResourceServerType,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        super(authenticatedResourceServerType);
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public Mono<Token> call() {
        return LocalAuthentication.current()
                .flatMap(authentication -> {
                    Mono<OAuth2AuthorizedClient> authorizedClient = authorizedClientService.loadAuthorizedClient(
                            authentication.getAuthorizedClientRegistrationId(),
                            authentication.getName());
                    return authorizedClient
                            .switchIfEmpty(Mono.error(new AccessDeniedException(
                                    "Fant ikke lokal Azure AD-sesjon.")))
                            .map(client -> Token.builder()
                                    .clientCredentials(false)
                                    .userId(authentication.getName())
                                    .accessTokenValue(client.getAccessToken().getTokenValue())
                                    .expiresAt(client.getAccessToken().getExpiresAt())
                                    .build());
                });
    }
}

final class LocalAuthentication {

    private LocalAuthentication() {
    }

    static Mono<OAuth2AuthenticationToken> current() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .ofType(OAuth2AuthenticationToken.class)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Lokal Azure AD-innlogging mangler.")));
    }
}
