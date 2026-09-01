package no.nav.dolly.web.service;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.TestnavBrukerServiceProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.securitycore.config.UserSessionConstant;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserJwtResolverTest {

    private static final String AZURE_USER_ID = "00000000-0000-0000-0000-000000000000";
    private static final String HASHED_SESSION_USER_ID = "fake-hashed-session-user-id";

    @Mock
    private GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    @Mock
    private GetAuthenticatedUserId getAuthenticatedUserId;

    @Mock
    private UserJwtExchange userJwtExchange;

    @Mock
    private AccessService accessService;

    private TestnavBrukerServiceProperties brukerServiceProperties;
    private UserJwtResolver userJwtResolver;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        brukerServiceProperties = new TestnavBrukerServiceProperties();
        userJwtResolver = new UserJwtResolver(
                getAuthenticatedResourceServerType,
                getAuthenticatedUserId,
                userJwtExchange,
                accessService,
                brukerServiceProperties);
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"));
    }

    @Test
    void shouldUseAzureFlowForAzurePrincipal() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(AZURE_USER_ID));
        when(accessService.getAccessToken(brukerServiceProperties, exchange)).thenReturn(Mono.just("obo-token"));
        when(userJwtExchange.generateJwtWithAccessToken(AZURE_USER_ID, "obo-token"))
                .thenReturn(Mono.just("azure-user-jwt"));

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectNext("azure-user-jwt")
                .verifyComplete();

        verify(userJwtExchange).generateJwtWithAccessToken(AZURE_USER_ID, "obo-token");
    }

    @Test
    void shouldKeepUsingHashedSessionUserIdForIdporten() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.TOKEN_X));
        exchange.getSession().block().getAttributes()
                .put(UserSessionConstant.SESSION_USER_ID_KEY, HASHED_SESSION_USER_ID);
        when(userJwtExchange.generateJwt(HASHED_SESSION_USER_ID, exchange)).thenReturn(Mono.just("idporten-user-jwt"));

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectNext("idporten-user-jwt")
                .verifyComplete();

        verify(userJwtExchange).generateJwt(HASHED_SESSION_USER_ID, exchange);
        verify(getAuthenticatedUserId, never()).call();
        verify(userJwtExchange, never()).generateJwtWithAccessToken(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldPreserveMissingUserJwtBeforeIdportenOrganizationSelection() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.TOKEN_X));

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .verifyComplete();

        verify(userJwtExchange, never()).generateJwt(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(ServerWebExchange.class));
    }

    @Test
    void shouldNeverUseAuthenticatedPidAsIdportenUserJwtId() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.TOKEN_X));
        exchange.getSession().block().getAttributes()
                .put(UserSessionConstant.SESSION_USER_ID_KEY, "41010100044");

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectError(AccessDeniedException.class)
                .verify();

        verify(userJwtExchange, never()).generateJwt(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(ServerWebExchange.class));
        verify(getAuthenticatedUserId, never()).call();
    }

    @Test
    void shouldFailClosedWhenAzureUserJwtCannotBeGenerated() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(AZURE_USER_ID));
        when(accessService.getAccessToken(brukerServiceProperties, exchange)).thenReturn(Mono.just("obo-token"));
        when(userJwtExchange.generateJwtWithAccessToken(AZURE_USER_ID, "obo-token"))
                .thenReturn(Mono.error(new AccessDeniedException("rejected")));

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldFailClosedWhenResourceServerTypeIsMissing() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.empty());

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldFailClosedWhenAzureUserIdIsMissing() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(getAuthenticatedUserId.call()).thenReturn(Mono.empty());
        when(accessService.getAccessToken(brukerServiceProperties, exchange)).thenReturn(Mono.just("obo-token"));

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldFailClosedWhenAzureAccessTokenIsMissing() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(AZURE_USER_ID));
        when(accessService.getAccessToken(brukerServiceProperties, exchange)).thenReturn(Mono.empty());

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldFailClosedWhenGeneratedAzureUserJwtIsMissing() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(AZURE_USER_ID));
        when(accessService.getAccessToken(brukerServiceProperties, exchange)).thenReturn(Mono.just("obo-token"));
        when(userJwtExchange.generateJwtWithAccessToken(AZURE_USER_ID, "obo-token")).thenReturn(Mono.empty());

        StepVerifier.create(userJwtResolver.resolve(exchange))
                .expectError(AccessDeniedException.class)
                .verify();
    }
}
