package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryTokenResolverTest {

    @Mock
    private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    private InMemoryTokenResolver inMemoryTokenResolver;
    private ServerWebExchange exchange;

    private OAuth2AuthenticationToken buildOAuth2AuthenticationToken() {
        var user = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("sub", "test-user", "pid", "12345678901"),
                "sub"
        );
        return new OAuth2AuthenticationToken(
                user,
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                "idporten"
        );
    }

    private ClientRegistration buildClientRegistration() {
        return ClientRegistration.withRegistrationId("idporten")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("test-client-id")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://idporten.no/authorize")
                .tokenUri("https://idporten.no/token")
                .build();
    }

    @BeforeEach
    void setUp() {
        inMemoryTokenResolver = new InMemoryTokenResolver(authorizedClientManager);
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
    }

    @Test
    void shouldReturnTokenWhenManagerReturnsAuthorizedClient() {
        var accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        var refreshToken = new OAuth2RefreshToken("refresh-token-value", Instant.now());
        var clientRegistration = buildClientRegistration();
        var authorizedClient = new OAuth2AuthorizedClient(
                clientRegistration, "user", accessToken, refreshToken
        );

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(Mono.just(authorizedClient));

        var authToken = buildOAuth2AuthenticationToken();
        var securityContext = new SecurityContextImpl(authToken);

        StepVerifier.create(
                        inMemoryTokenResolver.getToken(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .assertNext(token -> {
                    assertThat(token.getAccessTokenValue()).isEqualTo("access-token-value");
                    assertThat(token.getRefreshTokenValue()).isEqualTo("refresh-token-value");
                    assertThat(token.isClientCredentials()).isFalse();
                    assertThat(token.getExpiresAt()).isNotNull();
                })
                .verifyComplete();

        verify(authorizedClientManager).authorize(any(OAuth2AuthorizeRequest.class));
    }

    @Test
    void shouldReturnErrorWhenManagerReturnsEmpty() {
        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(Mono.empty());

        var authToken = buildOAuth2AuthenticationToken();
        var securityContext = new SecurityContextImpl(authToken);

        StepVerifier.create(
                        inMemoryTokenResolver.getToken(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .expectError(CredentialsExpiredException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenManagerThrows() {
        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Refresh failed")));

        var authToken = buildOAuth2AuthenticationToken();
        var securityContext = new SecurityContextImpl(authToken);

        StepVerifier.create(
                        inMemoryTokenResolver.getToken(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleTokenWithoutRefreshToken() {
        var accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-only",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        var clientRegistration = buildClientRegistration();
        var authorizedClient = new OAuth2AuthorizedClient(
                clientRegistration, "user", accessToken, null
        );

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(Mono.just(authorizedClient));

        var authToken = buildOAuth2AuthenticationToken();
        var securityContext = new SecurityContextImpl(authToken);

        StepVerifier.create(
                        inMemoryTokenResolver.getToken(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .assertNext(token -> {
                    assertThat(token.getAccessTokenValue()).isEqualTo("access-only");
                    assertThat(token.getRefreshTokenValue()).isNull();
                    assertThat(token.isClientCredentials()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void shouldDelegateWithCorrectRegistrationId() {
        var accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        var clientRegistration = buildClientRegistration();
        var authorizedClient = new OAuth2AuthorizedClient(
                clientRegistration, "user", accessToken
        );

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenAnswer(invocation -> {
                    OAuth2AuthorizeRequest request = invocation.getArgument(0);
                    assertThat(request.getClientRegistrationId()).isEqualTo("idporten");
                    assertThat(request.getPrincipal()).isNotNull();
                    return Mono.just(authorizedClient);
                });

        var authToken = buildOAuth2AuthenticationToken();
        var securityContext = new SecurityContextImpl(authToken);

        StepVerifier.create(
                        inMemoryTokenResolver.getToken(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .expectNextCount(1)
                .verifyComplete();
    }
}
